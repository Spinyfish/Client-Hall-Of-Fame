/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.sponge.handlers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelEncoderException;
import com.viaversion.viaversion.handlers.ChannelHandlerContextWrapper;
import com.viaversion.viaversion.handlers.ViaCodecHandler;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.InvocationTargetException;

public class SpongeEncodeHandler
extends MessageToByteEncoder<Object>
implements ViaCodecHandler {
    private final UserConnection info;
    private final MessageToByteEncoder<?> minecraftEncoder;

    public SpongeEncodeHandler(UserConnection info, MessageToByteEncoder<?> minecraftEncoder) {
        this.info = info;
        this.minecraftEncoder = minecraftEncoder;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf bytebuf) throws Exception {
        if (!(o instanceof ByteBuf)) {
            try {
                PipelineUtil.callEncode(this.minecraftEncoder, new ChannelHandlerContextWrapper(ctx, this), o, bytebuf);
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof Exception) {
                    throw (Exception)e.getCause();
                }
                if (!(e.getCause() instanceof Error)) ** GOTO lbl12
                throw (Error)e.getCause();
            }
        } else {
            bytebuf.writeBytes((ByteBuf)o);
        }
lbl12:
        // 3 sources

        this.transform(bytebuf);
    }

    @Override
    public void transform(ByteBuf bytebuf) throws Exception {
        if (!this.info.checkClientboundPacket()) {
            throw CancelEncoderException.generate(null);
        }
        if (!this.info.shouldTransformPacket()) {
            return;
        }
        this.info.transformClientbound(bytebuf, CancelEncoderException::generate);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CancelCodecException) {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}

