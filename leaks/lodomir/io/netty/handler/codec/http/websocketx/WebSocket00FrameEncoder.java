/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class WebSocket00FrameEncoder
extends MessageToMessageEncoder<WebSocketFrame>
implements WebSocketFrameEncoder {
    private static final ByteBuf _0X00 = Unpooled.unreleasableBuffer(Unpooled.directBuffer(1, 1).writeByte(0));
    private static final ByteBuf _0XFF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(1, 1).writeByte(-1));
    private static final ByteBuf _0XFF_0X00 = Unpooled.unreleasableBuffer(Unpooled.directBuffer(2, 2).writeByte(-1).writeByte(0));

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (msg instanceof TextWebSocketFrame) {
            ByteBuf data = msg.content();
            out.add(_0X00.duplicate());
            out.add(data.retain());
            out.add(_0XFF.duplicate());
        } else if (msg instanceof CloseWebSocketFrame) {
            out.add(_0XFF_0X00.duplicate());
        } else {
            ByteBuf data = msg.content();
            int dataLen = data.readableBytes();
            ByteBuf buf = ctx.alloc().buffer(5);
            boolean release = true;
            try {
                buf.writeByte(-128);
                int b1 = dataLen >>> 28 & 0x7F;
                int b2 = dataLen >>> 14 & 0x7F;
                int b3 = dataLen >>> 7 & 0x7F;
                int b4 = dataLen & 0x7F;
                if (b1 == 0) {
                    if (b2 == 0) {
                        if (b3 == 0) {
                            buf.writeByte(b4);
                        } else {
                            buf.writeByte(b3 | 0x80);
                            buf.writeByte(b4);
                        }
                    } else {
                        buf.writeByte(b2 | 0x80);
                        buf.writeByte(b3 | 0x80);
                        buf.writeByte(b4);
                    }
                } else {
                    buf.writeByte(b1 | 0x80);
                    buf.writeByte(b2 | 0x80);
                    buf.writeByte(b3 | 0x80);
                    buf.writeByte(b4);
                }
                out.add(buf);
                out.add(data.retain());
                release = false;
            }
            finally {
                if (release) {
                    buf.release();
                }
            }
        }
    }
}

