package org.revo.streamer.codec;


import org.revo.streamer.codec.aac.AdtsFrame;
import org.revo.streamer.codec.rtp.RtpPkt;
import org.revo.streamer.codec.util.StaticProcs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RtpPktToAdtsFrame implements Function<RtpPkt, List<AdtsFrame>> {
    @Override
    public synchronized List<AdtsFrame> apply(RtpPkt rtpPkt) {
        List<AdtsFrame> adts = new ArrayList<>();
        if (rtpPkt.getPayloadType() != 97) return adts;
        int auHeaderLength = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), 0) >> 3;
        int offset = 2 + auHeaderLength;
        for (int i = 0; i < (auHeaderLength / 2); i++) {
            int size = StaticProcs.bytesToUIntInt(rtpPkt.getPayload(), 2 + (i * 2)) >> 3;
            adts.add(new AdtsFrame(rtpPkt.getPayload(), offset, size));
            offset += size;
        }
        return adts;
    }
}
