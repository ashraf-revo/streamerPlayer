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
    private int lastSeqNumber = 0;

    public RtpPktToNalu() {
    }

    @Override
    public List<NALU> apply(RtpPkt rtpPkt) {
        if (rtpPkt.getPayloadType() != 96) return nalus;
        if (this.lastSeqNumber > rtpPkt.getSeqNumber()) {
            fuNalU = null;
            return nalus;
        }
        this.lastSeqNumber = rtpPkt.getSeqNumber();
        NaluHeader naluHeader = NaluHeader.read(rtpPkt.getPayload()[0]);
        if (naluHeader.getTYPE() > 0 && naluHeader.getTYPE() <= 23) {
            return Collections.singletonList(new NALU(rtpPkt.getPayload(), 0, rtpPkt.getPayload().length));
        } else if (naluHeader.getTYPE() == 24) {
            nalus = new ArrayList<>();
            int index = 1;
            while (index < rtpPkt.getPayload().length - 1 /*NAL Unit-0 Header*/) {
                int size = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), index);
                index += 2;   //                NAL Unit-i Size
                nalus.add(new NALU(rtpPkt.getPayload(), index, size));
                index += size;//                NAL Unit-i Data
            }
            return nalus;
        } else if (naluHeader.getTYPE() == 28) {
            boolean start = ((rtpPkt.getPayload()[1] & 0x80) >> 7) > 0;
            boolean end = ((rtpPkt.getPayload()[1] & 0x40) >> 6) > 0;
//            int reserved = (rtpPkt.getPayload()[1] & 0x20) >> 5;
            int type = (rtpPkt.getPayload()[1] & 0x1F);
            if (start) {
                this.fuNalU = new NALU(naluHeader.getF(), naluHeader.getNRI(), type, rtpPkt.getPayload(), 2);
            }
            if (fuNalU != null) {
                this.fuNalU.appendData(rtpPkt.getPayload(), 2);
            }
            if (end && fuNalU != null) {
                nalus = Collections.singletonList(new NALU(fuNalU.getData(), 0, fuNalU.getData().length));
                this.fuNalU = null;
                return nalus;
            }

        } else {
            System.out.println("Unknown Type " + naluHeader.getTYPE() + "  " + rtpPkt.getPayloadType());
        }
        return Collections.emptyList();
    }
}