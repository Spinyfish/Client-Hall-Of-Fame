/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.List;

public class WebSocketFrameAggregator
extends MessageToMessageDecoder<WebSocketFrame> {
    private final int maxFrameSize;
    private WebSocketFrame currentFrame;
    private boolean tooLongFrameFound;

    public WebSocketFrameAggregator(int maxFrameSize) {
        if (maxFrameSize < 1) {
            throw new IllegalArgumentException("maxFrameSize must be > 0");
        }
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (this.currentFrame == null) {
            this.tooLongFrameFound = false;
            if (msg.isFinalFragment()) {
                out.add(msg.retain());
                return;
            }
            CompositeByteBuf buf = ctx.alloc().compositeBuffer().addComponent(msg.content().retain());
            ((ByteBuf)buf).writerIndex(((ByteBuf)buf).writerIndex() + msg.content().readableBytes());
            if (msg instanceof TextWebSocketFrame) {
                this.currentFrame = new TextWebSocketFrame(true, msg.rsv(), buf);
            } else if (msg instanceof BinaryWebSocketFrame) {
                this.currentFrame = new BinaryWebSocketFrame(true, msg.rsv(), buf);
            } else {
                buf.release();
                throw new IllegalStateException("WebSocket frame was not of type TextWebSocketFrame or BinaryWebSocketFrame");
            }
            return;
        }
        if (msg instanceof ContinuationWebSocketFrame) {
            if (this.tooLongFrameFound) {
                if (msg.isFinalFragment()) {
                    this.currentFrame = null;
                }
                return;
            }
            CompositeByteBuf content = (CompositeByteBuf)this.currentFrame.content();
            if (content.readableBytes() > this.maxFrameSize - msg.content().readableBytes()) {
                this.currentFrame.release();
                this.tooLongFrameFound = true;
                throw new TooLongFrameException("WebSocketFrame length exceeded " + content + " bytes.");
            }
            content.addComponent(msg.content().retain());
            content.writerIndex(content.writerIndex() + msg.content().readableBytes());
            if (msg.isFinalFragment()) {
                WebSocketFrame currentFrame = this.currentFrame;
                this.currentFrame = null;
                out.add(currentFrame);
                return;
            }
            return;
        }
        out.add(msg.retain());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (this.currentFrame != null) {
            this.currentFrame.release();
            this.currentFrame = null;
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        if (this.currentFrame != null) {
            this.currentFrame.release();
            this.currentFrame = null;
        }
    }
}

