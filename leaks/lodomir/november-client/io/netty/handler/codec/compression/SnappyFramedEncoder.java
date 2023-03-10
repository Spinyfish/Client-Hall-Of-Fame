/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.Snappy;

public class SnappyFramedEncoder
extends MessageToByteEncoder<ByteBuf> {
    private static final int MIN_COMPRESSIBLE_LENGTH = 18;
    private static final byte[] STREAM_START = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private final Snappy snappy = new Snappy();
    private boolean started;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        block6: {
            int dataLength;
            if (!in.isReadable()) {
                return;
            }
            if (!this.started) {
                this.started = true;
                out.writeBytes(STREAM_START);
            }
            if ((dataLength = in.readableBytes()) > 18) {
                ByteBuf slice;
                int lengthIdx;
                while (true) {
                    lengthIdx = out.writerIndex() + 1;
                    if (dataLength < 18) {
                        slice = in.readSlice(dataLength);
                        SnappyFramedEncoder.writeUnencodedChunk(slice, out, dataLength);
                        break block6;
                    }
                    out.writeInt(0);
                    if (dataLength <= Short.MAX_VALUE) break;
                    slice = in.readSlice(Short.MAX_VALUE);
                    SnappyFramedEncoder.calculateAndWriteChecksum(slice, out);
                    this.snappy.encode(slice, out, Short.MAX_VALUE);
                    SnappyFramedEncoder.setChunkLength(out, lengthIdx);
                    dataLength -= Short.MAX_VALUE;
                }
                slice = in.readSlice(dataLength);
                SnappyFramedEncoder.calculateAndWriteChecksum(slice, out);
                this.snappy.encode(slice, out, dataLength);
                SnappyFramedEncoder.setChunkLength(out, lengthIdx);
            } else {
                SnappyFramedEncoder.writeUnencodedChunk(in, out, dataLength);
            }
        }
    }

    private static void writeUnencodedChunk(ByteBuf in, ByteBuf out, int dataLength) {
        out.writeByte(1);
        SnappyFramedEncoder.writeChunkLength(out, dataLength + 4);
        SnappyFramedEncoder.calculateAndWriteChecksum(in, out);
        out.writeBytes(in, dataLength);
    }

    private static void setChunkLength(ByteBuf out, int lengthIdx) {
        int chunkLength = out.writerIndex() - lengthIdx - 3;
        if (chunkLength >>> 24 != 0) {
            throw new CompressionException("compressed data too large: " + chunkLength);
        }
        out.setMedium(lengthIdx, ByteBufUtil.swapMedium(chunkLength));
    }

    private static void writeChunkLength(ByteBuf out, int chunkLength) {
        out.writeMedium(ByteBufUtil.swapMedium(chunkLength));
    }

    private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
        out.writeInt(ByteBufUtil.swapInt(Snappy.calculateChecksum(slice)));
    }
}

