package org.revo.streamer.codec.aac;

public class AdtsFrame {
    private byte[] payload;
    private byte[] header = new byte[]{(byte) 0xFF, (byte) 0xF1, (byte) 0x4C, (byte) 0x80, (byte) 0x2F, (byte) 0x5F, (byte) 0xFC};

    public AdtsFrame(byte[] payload, int offset, int size) {
        initSize(size);
        this.payload = new byte[size];
        System.arraycopy(payload, offset, this.payload, 0, this.payload.length);
    }


    private void initSize(int size) {
        size += header.length;
        header[3] |= (byte) ((size & 0x1800) >> 11);
        header[4] = (byte) ((size & 0x1FF8) >> 3);
        header[5] = (byte) ((size & 0x7) << 5);
    }

    public byte[] getHeader() {
        return header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getRaw() {
        byte raw[] = new byte[payload.length + header.length];
        System.arraycopy(header, 0, raw, 0, header.length);
        System.arraycopy(payload, 0, raw, header.length, payload.length);
        return raw;
    }
}
