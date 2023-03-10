/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.CodedInputStream
 */
package io.netty.handler.codec.protobuf;

import com.google.protobuf.CodedInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class ProtobufVarint32FrameDecoder
extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        byte[] buf = new byte[5];
        for (int i = 0; i < buf.length; ++i) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }
            buf[i] = in.readByte();
            if (buf[i] < 0) continue;
            int length = CodedInputStream.newInstance((byte[])buf, (int)0, (int)(i + 1)).readRawVarint32();
            if (length < 0) {
                throw new CorruptedFrameException("negative length: " + length);
            }
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }
            out.add(in.readBytes(length));
            return;
        }
        throw new CorruptedFrameException("length wider than 32-bit");
    }
}

