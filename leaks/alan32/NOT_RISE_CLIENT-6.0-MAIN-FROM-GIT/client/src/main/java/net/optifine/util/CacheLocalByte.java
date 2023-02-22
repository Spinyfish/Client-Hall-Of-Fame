package net.optifine.util;

public class CacheLocalByte {
    private int maxX = 18;
    private int maxY = 128;
    private int maxZ = 18;
    private int offsetX = 0;
    private int offsetY = 0;
    private int offsetZ = 0;
    private byte[][][] cache = null;
    private byte[] lastZs = null;
    private int lastDz = 0;

    public CacheLocalByte(final int maxX, final int maxY, final int maxZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.cache = new byte[maxX][maxY][maxZ];
        this.resetCache();
    }

    public void resetCache() {
        for (int i = 0; i < this.maxX; ++i) {
            final byte[][] abyte = this.cache[i];

            for (int j = 0; j < this.maxY; ++j) {
                final byte[] abyte1 = abyte[j];

                for (int k = 0; k < this.maxZ; ++k) {
                    abyte1[k] = -1;
                }
            }
        }
    }

    public void setOffset(final int x, final int y, final int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        this.resetCache();
    }

    public byte get(final int x, final int y, final int z) {
        try {
            this.lastZs = this.cache[x - this.offsetX][y - this.offsetY];
            this.lastDz = z - this.offsetZ;
            return this.lastZs[this.lastDz];
        } catch (final ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
            arrayindexoutofboundsexception.printStackTrace();
            return (byte) -1;
        }
    }

    public void setLast(final byte val) {
        try {
            this.lastZs[this.lastDz] = val;
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}
