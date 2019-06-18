package org.revo.streamer.codec;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AacPlayer {
    private AudioFormat aufmt = new AudioFormat(48000, 16, 2, true, true);
    private SourceDataLine line = AudioSystem.getSourceDataLine(aufmt);
    private Decoder dec = new Decoder(new byte[]{17, -112});

    private SampleBuffer buf = new SampleBuffer();

    public AacPlayer() throws AACException, LineUnavailableException {
        line.open();
        line.start();
    }


    public void play(byte[] bytes) throws AACException {
        dec.decodeFrame(bytes, buf);
        line.write(buf.getData(), 0, buf.getData().length);

    }
}
