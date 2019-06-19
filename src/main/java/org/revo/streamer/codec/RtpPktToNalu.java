package org.revo.streamer.codec;


import org.revo.streamer.codec.h264.NALU;
import org.revo.streamer.codec.h264.NALU.NaluHeader;
import org.revo.streamer.codec.rtp.RtpPkt;
import org.revo.streamer.codec.util.StaticProcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class RtpPktToNalu implements Function<RtpPkt, List<NALU>> {
    private List<NALU> nalus = new ArrayList<>();
    private NALU fuNalU = null;

    public RtpPktToNalu() {
    }

    @Override
    public synchronized List<NALU> apply(RtpPkt rtpPkt) {
        if (rtpPkt.getPayloadType() != 96) return nalus;
        NaluHeader naluHeader = NaluHeader.read(rtpPkt.getPayload()[0]);
        if (naluHeader.getTYPE() > 0 && naluHeader.getTYPE() <= 23) {
            return Collections.singletonList(new NALU(rtpPkt.getPayload(), 0, rtpPkt.getPayload().length));
        } else if (naluHeader.getTYPE() == 24) {
            nalus = new ArrayList<>();
            int offset = 1;
            while (offset < rtpPkt.getPayload().length - 1 /*NAL Unit-0 Header*/) {
                int size = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), offset);
                offset += 2;   //                NAL Unit-i Size
                nalus.add(new NALU(rtpPkt.getPayload(), offset, size + offset));
                offset += size;//                NAL Unit-i Data
            }
            return nalus;
        } else if (naluHeader.getTYPE() == 28) {
            boolean start = ((rtpPkt.getPayload()[1] & 0x80) >> 7) > 0;
            boolean end = ((rtpPkt.getPayload()[1] & 0x40) >> 6) > 0;
//            int reserved = (rtpPkt.getPayload()[1] & 0x20) >> 5;
            int type = (rtpPkt.getPayload()[1] & 0x1F);
            if (start) {
                this.fuNalU = new NALU(naluHeader.getF(), naluHeader.getNRI(), type);
                this.fuNalU.appendData(rtpPkt.getPayload(), 2);
            }
            if (this.fuNalU != null && this.fuNalU.getNaluHeader().getTYPE() == type) {
                if (!start) {
                    this.fuNalU.appendData(rtpPkt.getPayload(), 2);
                }
                if (end) {
                    nalus = Collections.singletonList(new NALU(fuNalU.getData(), 0, fuNalU.getData().length));
                    this.fuNalU = null;
                    return nalus;
                }
            }
        } else {
            System.out.println("Unknown Type " + naluHeader.getTYPE());
        }
        return Collections.emptyList();
    }
}