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
import java.util.function.Consumer;

public class AacMessageHandler implements MessageHandler, Consumer<AdtsFrame> {
    private FileOutputStream stream = new FileOutputStream(new File("out.aac"));
    private RtpPktToAdtsFrame rtpPktToAdtsFrame = new RtpPktToAdtsFrame();

    public AacMessageHandler() throws FileNotFoundException {
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        rtpPktToAdtsFrame.apply(new RtpPkt((byte[]) message.getPayload())).forEach(this);
    }

    @Override
    public void accept(AdtsFrame adtsFrame) {
        try {
            stream.write(adtsFrame.getRaw());
            stream.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
