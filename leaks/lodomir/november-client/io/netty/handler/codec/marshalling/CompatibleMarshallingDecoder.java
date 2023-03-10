/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.ByteInput
 *  org.jboss.marshalling.Unmarshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.marshalling.ChannelBufferByteInput;
import io.netty.handler.codec.marshalling.LimitingByteInput;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import java.util.List;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

public class CompatibleMarshallingDecoder
extends ReplayingDecoder<Void> {
    protected final UnmarshallerProvider provider;
    protected final int maxObjectSize;
    private boolean discardingTooLongFrame;

    public CompatibleMarshallingDecoder(UnmarshallerProvider provider, int maxObjectSize) {
        this.provider = provider;
        this.maxObjectSize = maxObjectSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (this.discardingTooLongFrame) {
            buffer.skipBytes(this.actualReadableBytes());
            this.checkpoint();
            return;
        }
        Unmarshaller unmarshaller = this.provider.getUnmarshaller(ctx);
        Object input = new ChannelBufferByteInput(buffer);
        if (this.maxObjectSize != Integer.MAX_VALUE) {
            input = new LimitingByteInput((ByteInput)input, this.maxObjectSize);
        }
        try {
            unmarshaller.start((ByteInput)input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            out.add(obj);
        }
        catch (LimitingByteInput.TooBigObjectException ignored) {
            this.discardingTooLongFrame = true;
            throw new TooLongFrameException();
        }
        finally {
            unmarshaller.close();
        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        switch (buffer.readableBytes()) {
            case 0: {
                return;
            }
            case 1: {
                if (buffer.getByte(buffer.readerIndex()) != 121) break;
                buffer.skipBytes(1);
                return;
            }
        }
        this.decode(ctx, buffer, out);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}

