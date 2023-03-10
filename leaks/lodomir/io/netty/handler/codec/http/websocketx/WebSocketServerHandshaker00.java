/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00
extends WebSocketServerHandshaker {
    private static final Pattern BEGINNING_DIGIT = Pattern.compile("[^0-9]");
    private static final Pattern BEGINNING_SPACE = Pattern.compile("[^ ]");

    public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, int maxFramePayloadLength) {
        super(WebSocketVersion.V00, webSocketURL, subprotocols, maxFramePayloadLength);
    }

    @Override
    protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
        if (!"Upgrade".equalsIgnoreCase(req.headers().get("Connection")) || !"WebSocket".equalsIgnoreCase(req.headers().get("Upgrade"))) {
            throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
        }
        boolean isHixie76 = req.headers().contains("Sec-WebSocket-Key1") && req.headers().contains("Sec-WebSocket-Key2");
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"));
        if (headers != null) {
            res.headers().add(headers);
        }
        res.headers().add("Upgrade", (Object)"WebSocket");
        res.headers().add("Connection", (Object)"Upgrade");
        if (isHixie76) {
            res.headers().add("Sec-WebSocket-Origin", (Object)req.headers().get("Origin"));
            res.headers().add("Sec-WebSocket-Location", (Object)this.uri());
            String subprotocols = req.headers().get("Sec-WebSocket-Protocol");
            if (subprotocols != null) {
                String selectedSubprotocol = this.selectSubprotocol(subprotocols);
                if (selectedSubprotocol == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Requested subprotocol(s) not supported: {}", (Object)subprotocols);
                    }
                } else {
                    res.headers().add("Sec-WebSocket-Protocol", (Object)selectedSubprotocol);
                }
            }
            String key1 = req.headers().get("Sec-WebSocket-Key1");
            String key2 = req.headers().get("Sec-WebSocket-Key2");
            int a = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(key1).replaceAll("")) / (long)BEGINNING_SPACE.matcher(key1).replaceAll("").length());
            int b = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(key2).replaceAll("")) / (long)BEGINNING_SPACE.matcher(key2).replaceAll("").length());
            long c = req.content().readLong();
            ByteBuf input = Unpooled.buffer(16);
            input.writeInt(a);
            input.writeInt(b);
            input.writeLong(c);
            res.content().writeBytes(WebSocketUtil.md5(input.array()));
        } else {
            res.headers().add("WebSocket-Origin", (Object)req.headers().get("Origin"));
            res.headers().add("WebSocket-Location", (Object)this.uri());
            String protocol = req.headers().get("WebSocket-Protocol");
            if (protocol != null) {
                res.headers().add("WebSocket-Protocol", (Object)this.selectSubprotocol(protocol));
            }
        }
        return res;
    }

    @Override
    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
        return channel.writeAndFlush(frame, promise);
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
}

