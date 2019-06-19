package org.revo.streamer.codec.h264;


public class NALU {
    private byte[] data;
    private NaluHeader naluHeader;

    public NALU(byte[] data, int offset, int length) {
        this.data = new byte[length - offset];
        System.arraycopy(data, offset, this.data, 0, this.data.length);
        this.naluHeader = NaluHeader.read(data[offset]);
    }


    public NALU(int F, int NRI, int TYPE) {
        this.data = new byte[1];
        this.naluHeader = NaluHeader.from(F, NRI, TYPE);
        this.data[0] = this.naluHeader.getRaw();
    }


    public byte[] getData() {
        return data;
    }

    public void appendData(byte data[], int offset) {
        byte[] ndata = new byte[data.length - offset];
        System.arraycopy(data, offset, ndata, 0, ndata.length);
        this.data = copyOfAndAppend(this.data, ndata);
    }

    private static byte[] copyOfAndAppend(byte[] data1, byte[] data2) {
        byte[] result = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, result, 0, data1.length);
        System.arraycopy(data2, 0, result, data1.length, data2.length);
        return result;
    }

    public NaluHeader getNaluHeader() {
        return naluHeader;
    }

    public static class NaluHeader {
        private int F;
        private int NRI;
        private int TYPE;

        private NaluHeader() {

        }

        NaluHeader(int F, int NRI, int TYPE) {
            this.F = F;
            this.NRI = NRI;
            this.TYPE = TYPE;
        }

        public static NaluHeader read(byte b) {
            NaluHeader naluHeader = new NaluHeader();
            naluHeader.F = (b & 0x80) >> 7;
            naluHeader.NRI = (b & 0x60) >> 5;
            naluHeader.TYPE = b & 0x1F;
            return naluHeader;
        }

        @Override
        public String toString() {
            return "NaluHeader{" +
                    "F=" + F +
                    ", NRI=" + NRI +
                    ", TYPE=" + TYPE +
                    '}';
        }

        static NaluHeader from(int F, int NRI, int TYPE) {
            return new NaluHeader(F, NRI, TYPE);
        }

        public int getF() {
            return F;
        }

        public int getNRI() {
            return NRI;
        }

        public int getTYPE() {
            return TYPE;
        }

        byte getRaw() {
            int i = ((this.F << 7) | (this.NRI << 5) | (this.TYPE & 0x1F)) & 0xFF;
            return ((byte) i);
        }
    }
}
