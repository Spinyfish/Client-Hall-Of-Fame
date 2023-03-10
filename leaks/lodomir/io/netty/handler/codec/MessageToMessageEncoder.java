/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
    }

    protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        RecyclableArrayList out = null;
        try {
            if (this.acceptOutboundMessage(msg)) {
                out = RecyclableArrayList.newInstance();
                Object cast = msg;
                try {
                    this.encode(ctx, cast, out);
                }
                finally {
                    ReferenceCountUtil.release(cast);
                }
                if (out.isEmpty()) {
                    out.recycle();
                    out = null;
                    throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
                }
            } else {
                ctx.write(msg, promise);
            }
            if (out == null) return;
        }
        catch (EncoderException e) {
            try {
                throw e;
                catch (Throwable t) {
                    throw new EncoderException(t);
                }
            }
            catch (Throwable throwable) {
                if (out == null) throw throwable;
                int sizeMinusOne = out.size() - 1;
                if (sizeMinusOne == 0) {
                    ctx.write(out.get(0), promise);
                } else if (sizeMinusOne > 0) {
                    ChannelPromise voidPromise = ctx.voidPromise();
                    boolean isVoidPromise = promise == voidPromise;
                    for (int i = 0; i < sizeMinusOne; ++i) {
                        ChannelPromise p = isVoidPromise ? voidPromise : ctx.newPromise();
                        ctx.write(out.get(i), p);
                    }
                    ctx.write(out.get(sizeMinusOne), promise);
                }
                out.recycle();
                throw throwable;
            }
        }
        int sizeMinusOne = out.size() - 1;
        if (sizeMinusOne == 0) {
            ctx.write(out.get(0), promise);
        } else if (sizeMinusOne > 0) {
            ChannelPromise voidPromise = ctx.voidPromise();
            boolean isVoidPromise = promise == voidPromise;
            for (int i = 0; i < sizeMinusOne; ++i) {
                ChannelPromise p = isVoidPromise ? voidPromise : ctx.newPromise();
                ctx.write(out.get(i), p);
            }
            ctx.write(out.get(sizeMinusOne), promise);
        }
        out.recycle();
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

