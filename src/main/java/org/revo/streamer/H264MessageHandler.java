package org.revo.streamer;

import org.revo.streamer.codec.RtpPktToNalu;
import org.revo.streamer.codec.h264.NALU;
import org.revo.streamer.codec.rtp.RtpPkt;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;

public class H264MessageHandler implements MessageHandler, BiConsumer<RtpPkt, NALU> {
    private FileOutputStream stream = new FileOutputStream("out.h264");
    private RtpPktToNalu rtpPktToNalu = new RtpPktToNalu();

    public H264MessageHandler() throws FileNotFoundException {
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        RtpPkt rtpPkt = new RtpPkt((byte[]) message.getPayload());
        rtpPktToNalu.apply(rtpPkt).forEach(it -> accept(rtpPkt, it));
    }


    @Override
    public synchronized void accept(RtpPkt rtpPkt, NALU nalu) {
        write(rtpPkt, nalu);
    }

    private void write(RtpPkt rtpPkt, NALU nalu) {
        try {
            stream.write(new byte[]{0x00, 0x00, 0x00, 0x01});
            stream.write(nalu.getData());
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
