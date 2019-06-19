package org.revo.streamer;

import org.revo.streamer.codec.RtpPktToAdtsFrame;
import org.revo.streamer.codec.aac.AdtsFrame;
import org.revo.streamer.codec.rtp.RtpPkt;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;

public class AacMessageHandler implements MessageHandler, BiConsumer<RtpPkt, AdtsFrame> {
    private FileOutputStream stream = new FileOutputStream(new File("out.aac"));
    private RtpPktToAdtsFrame rtpPktToAdtsFrame = new RtpPktToAdtsFrame();

    public AacMessageHandler() throws FileNotFoundException {
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        RtpPkt rtpPkt = new RtpPkt((byte[]) message.getPayload());
        rtpPktToAdtsFrame.apply(rtpPkt).forEach(it -> accept(rtpPkt, it));
    }

    @Override
    public synchronized void accept(RtpPkt rtpPkt, AdtsFrame adtsFrame) {
        try {
            stream.write(adtsFrame.getRaw());
            stream.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
