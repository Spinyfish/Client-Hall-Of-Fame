package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private RandomAccessFile dataFile;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private List<Boolean> sectorFree;

    /**
     * McRegion sizeDelta
     */
    private int sizeDelta;
    private long lastModified;

    public RegionFile(final File fileNameIn) {
        this.fileName = fileNameIn;
        this.sizeDelta = 0;

        try {
            if (fileNameIn.exists()) {
                this.lastModified = fileNameIn.lastModified();
            }

            this.dataFile = new RandomAccessFile(fileNameIn, "rw");

            if (this.dataFile.length() < 4096L) {
                for (int i = 0; i < 1024; ++i) {
                    this.dataFile.writeInt(0);
                }

                for (int i1 = 0; i1 < 1024; ++i1) {
                    this.dataFile.writeInt(0);
                }

                this.sizeDelta += 8192;
            }

            if ((this.dataFile.length() & 4095L) != 0L) {
                for (int j1 = 0; (long) j1 < (this.dataFile.length() & 4095L); ++j1) {
                    this.dataFile.write(0);
                }
            }

            final int k1 = (int) this.dataFile.length() / 4096;
            this.sectorFree = Lists.newArrayListWithCapacity(k1);

            for (int j = 0; j < k1; ++j) {
                this.sectorFree.add(Boolean.valueOf(true));
            }

            this.sectorFree.set(0, Boolean.valueOf(false));
            this.sectorFree.set(1, Boolean.valueOf(false));
            this.dataFile.seek(0L);

            for (int l1 = 0; l1 < 1024; ++l1) {
                final int k = this.dataFile.readInt();
                this.offsets[l1] = k;

                if (k != 0 && (k >> 8) + (k & 255) <= this.sectorFree.size()) {
                    for (int l = 0; l < (k & 255); ++l) {
                        this.sectorFree.set((k >> 8) + l, Boolean.valueOf(false));
                    }
                }
            }

            for (int i2 = 0; i2 < 1024; ++i2) {
                final int j2 = this.dataFile.readInt();
                this.chunkTimestamps[i2] = j2;
            }
        } catch (final IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    /**
     * Returns an uncompressed chunk stream from the region file.
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    public synchronized DataInputStream getChunkDataInputStream(final int x, final int z) {
        if (this.outOfBounds(x, z)) {
            return null;
        } else {
            try {
                final int i = this.getOffset(x, z);

                if (i == 0) {
                    return null;
                } else {
                    final int j = i >> 8;
                    final int k = i & 255;

                    if (j + k > this.sectorFree.size()) {
                        return null;
                    } else {
                        this.dataFile.seek(j * 4096);
                        final int l = this.dataFile.readInt();

                        if (l > 4096 * k) {
                            return null;
                        } else if (l <= 0) {
                            return null;
                        } else {
                            final byte b0 = this.dataFile.readByte();

                            if (b0 == 1) {
                                final byte[] abyte1 = new byte[l - 1];
                                this.dataFile.read(abyte1);
                                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte1))));
                            } else if (b0 == 2) {
                                final byte[] abyte = new byte[l - 1];
                                this.dataFile.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                            } else {
                                return null;
                            }
                        }
                    }
                }
            } catch (final IOException var9) {
                return null;
            }
        }
    }

    /**
     * Returns an output stream used to write chunk data. Data is on disk when the returned stream is closed.
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    public DataOutputStream getChunkDataOutputStream(final int x, final int z) {
        return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z)));
    }

    /**
     * args: x, z, data, length - write chunk data at (x, z) to disk
     *
     * @param x      Chunk X coordinate
     * @param z      Chunk Z coordinate
     * @param data   The chunk data to write
     * @param length The length of the data
     */
    protected synchronized void write(final int x, final int z, final byte[] data, final int length) {
        try {
            final int i = this.getOffset(x, z);
            int j = i >> 8;
            final int k = i & 255;
            final int l = (length + 5) / 4096 + 1;

            if (l >= 256) {
                return;
            }

            if (j != 0 && k == l) {
                this.write(j, data, length);
            } else {
                for (int i1 = 0; i1 < k; ++i1) {
                    this.sectorFree.set(j + i1, Boolean.valueOf(true));
                }

                int l1 = this.sectorFree.indexOf(Boolean.valueOf(true));
                int j1 = 0;

                if (l1 != -1) {
                    for (int k1 = l1; k1 < this.sectorFree.size(); ++k1) {
                        if (j1 != 0) {
                            if (this.sectorFree.get(k1).booleanValue()) {
                                ++j1;
                            } else {
                                j1 = 0;
                            }
                        } else if (this.sectorFree.get(k1).booleanValue()) {
                            l1 = k1;
                            j1 = 1;
                        }

                        if (j1 >= l) {
                            break;
                        }
                    }
                }

                if (j1 >= l) {
                    j = l1;
                    this.setOffset(x, z, l1 << 8 | l);

                    for (int j2 = 0; j2 < l; ++j2) {
                        this.sectorFree.set(j + j2, Boolean.valueOf(false));
                    }

                    this.write(j, data, length);
                } else {
                    this.dataFile.seek(this.dataFile.length());
                    j = this.sectorFree.size();

                    for (int i2 = 0; i2 < l; ++i2) {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(Boolean.valueOf(false));
                    }

                    this.sizeDelta += 4096 * l;
                    this.write(j, data, length);
                    this.setOffset(x, z, j << 8 | l);
                }
            }

            this.setChunkTimestamp(x, z, (int) (MinecraftServer.getCurrentTimeMillis() / 1000L));
        } catch (final IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    /**
     * args: sectorNumber, data, length - write the chunk data to this RegionFile
     */
    private void write(final int sectorNumber, final byte[] data, final int length) throws IOException {
        this.dataFile.seek(sectorNumber * 4096);
        this.dataFile.writeInt(length + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(data, 0, length);
    }

    /**
     * args: x, z - check region bounds
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    private boolean outOfBounds(final int x, final int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    /**
     * args: x, z - get chunk's offset in region file
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    private int getOffset(final int x, final int z) {
        return this.offsets[x + z * 32];
    }

    /**
     * args: x, z, - true if chunk has been saved / converted
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    public boolean isChunkSaved(final int x, final int z) {
        return this.getOffset(x, z) != 0;
    }

    /**
     * args: x, z, offset - sets the chunk's offset in the region file
     *
     * @param x      Chunk X coordinate
     * @param z      Chunk Z coordinate
     * @param offset The chunk offset
     */
    private void setOffset(final int x, final int z, final int offset) throws IOException {
        this.offsets[x + z * 32] = offset;
        this.dataFile.seek((x + z * 32) * 4);
        this.dataFile.writeInt(offset);
    }

    /**
     * args: x, z, timestamp - sets the chunk's write timestamp
     *
     * @param x         Chunk X coordinate
     * @param z         Chunk Z coordinate
     * @param timestamp The chunk's write timestamp
     */
    private void setChunkTimestamp(final int x, final int z, final int timestamp) throws IOException {
        this.chunkTimestamps[x + z * 32] = timestamp;
        this.dataFile.seek(4096 + (x + z * 32) * 4);
        this.dataFile.writeInt(timestamp);
    }

    /**
     * close this RegionFile and prevent further writes
     */
    public void close() throws IOException {
        if (this.dataFile != null) {
            this.dataFile.close();
        }
    }

    class ChunkBuffer extends ByteArrayOutputStream {
        private final int chunkX;
        private final int chunkZ;

        public ChunkBuffer(final int x, final int z) {
            super(8096);
            this.chunkX = x;
            this.chunkZ = z;
        }

        public void close() throws IOException {
            RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
        }
    }
}
