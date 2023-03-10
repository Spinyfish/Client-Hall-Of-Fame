/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Snappy;
import java.util.Arrays;
import java.util.List;

public class SnappyFramedDecoder
extends ByteToMessageDecoder {
    private static final byte[] SNAPPY = new byte[]{115, 78, 97, 80, 112, 89};
    private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
    private final Snappy snappy = new Snappy();
    private final boolean validateChecksums;
    private boolean started;
    private boolean corrupted;

    public SnappyFramedDecoder() {
        this(false);
    }

    public SnappyFramedDecoder(boolean validateChecksums) {
        this.validateChecksums = validateChecksums;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.corrupted) {
            in.skipBytes(in.readableBytes());
            return;
        }
        try {
            idx = in.readerIndex();
            inSize = in.readableBytes();
            if (inSize < 4) {
                return;
            }
            chunkTypeVal = in.getUnsignedByte(idx);
            chunkType = SnappyFramedDecoder.mapChunkType((byte)chunkTypeVal);
            chunkLength = ByteBufUtil.swapMedium(in.getUnsignedMedium(idx + 1));
            switch (1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[chunkType.ordinal()]) {
                case 1: {
                    if (chunkLength != SnappyFramedDecoder.SNAPPY.length) {
                        throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
                    }
                    if (inSize < 4 + SnappyFramedDecoder.SNAPPY.length) break;
                    identifier = new byte[chunkLength];
                    in.skipBytes(4).readBytes(identifier);
                    if (!Arrays.equals(identifier, SnappyFramedDecoder.SNAPPY)) {
                        throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
                    }
                    this.started = true;
                    break;
                }
                case 2: {
                    if (!this.started) {
                        throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4 + chunkLength);
                    break;
                }
                case 3: {
                    throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(chunkTypeVal));
                }
                case 4: {
                    if (!this.started) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 65540) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    if (this.validateChecksums) {
                        checksum = ByteBufUtil.swapInt(in.readInt());
                        Snappy.validateChecksum(checksum, in, in.readerIndex(), chunkLength - 4);
                    } else {
                        in.skipBytes(4);
                    }
                    out.add(in.readSlice(chunkLength - 4).retain());
                    break;
                }
                case 5: {
                    if (!this.started) {
                        throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    checksum = ByteBufUtil.swapInt(in.readInt());
                    uncompressed = ctx.alloc().buffer(0);
                    if (!this.validateChecksums) ** GOTO lbl75
                    oldWriterIndex = in.writerIndex();
                    try {
                        in.writerIndex(in.readerIndex() + chunkLength - 4);
                        this.snappy.decode(in, uncompressed);
                    }
                    finally {
                        in.writerIndex(oldWriterIndex);
                    }
                    Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
                    ** GOTO lbl76
lbl75:
                    // 1 sources

                    this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
lbl76:
                    // 2 sources

                    out.add(uncompressed);
                    this.snappy.reset();
                }
            }
        }
        catch (Exception e) {
            this.corrupted = true;
            throw e;
        }
    }

    private static ChunkType mapChunkType(byte type) {
        if (type == 0) {
            return ChunkType.COMPRESSED_DATA;
        }
        if (type == 1) {
            return ChunkType.UNCOMPRESSED_DATA;
        }
        if (type == -1) {
            return ChunkType.STREAM_IDENTIFIER;
        }
        if ((type & 0x80) == 128) {
            return ChunkType.RESERVED_SKIPPABLE;
        }
        return ChunkType.RESERVED_UNSKIPPABLE;
    }

    static class 1 {
        static final /* synthetic */ int[] $SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType;

        static {
            $SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType = new int[ChunkType.values().length];
            try {
                1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[ChunkType.STREAM_IDENTIFIER.ordinal()] = 1;
            }
            catch (NoSuchFieldError ex) {
                // empty catch block
            }
            try {
                1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[ChunkType.RESERVED_SKIPPABLE.ordinal()] = 2;
            }
            catch (NoSuchFieldError ex) {
                // empty catch block
            }
            try {
                1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[ChunkType.RESERVED_UNSKIPPABLE.ordinal()] = 3;
            }
            catch (NoSuchFieldError ex) {
                // empty catch block
            }
            try {
                1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[ChunkType.UNCOMPRESSED_DATA.ordinal()] = 4;
            }
            catch (NoSuchFieldError ex) {
                // empty catch block
            }
            try {
                1.$SwitchMap$io$netty$handler$codec$compression$SnappyFramedDecoder$ChunkType[ChunkType.COMPRESSED_DATA.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
        }
    }

    private static enum ChunkType {
        STREAM_IDENTIFIER,
        COMPRESSED_DATA,
        UNCOMPRESSED_DATA,
        RESERVED_UNSKIPPABLE,
        RESERVED_SKIPPABLE;

    }
}

