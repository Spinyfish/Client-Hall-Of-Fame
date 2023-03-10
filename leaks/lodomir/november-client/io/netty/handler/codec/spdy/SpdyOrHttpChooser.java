/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.spdy.SpdyFrameCodec;
import io.netty.handler.codec.spdy.SpdyHttpDecoder;
import io.netty.handler.codec.spdy.SpdyHttpEncoder;
import io.netty.handler.codec.spdy.SpdyHttpResponseStreamIdHandler;
import io.netty.handler.codec.spdy.SpdySessionHandler;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.internal.StringUtil;
import java.util.List;
import javax.net.ssl.SSLEngine;

public abstract class SpdyOrHttpChooser
extends ByteToMessageDecoder {
    private final int maxSpdyContentLength;
    private final int maxHttpContentLength;

    protected SpdyOrHttpChooser(int maxSpdyContentLength, int maxHttpContentLength) {
        this.maxSpdyContentLength = maxSpdyContentLength;
        this.maxHttpContentLength = maxHttpContentLength;
    }

    protected SelectedProtocol getProtocol(SSLEngine engine) {
        String[] protocol = StringUtil.split(engine.getSession().getProtocol(), ':');
        if (protocol.length < 2) {
            return SelectedProtocol.HTTP_1_1;
        }
        SelectedProtocol selectedProtocol = SelectedProtocol.protocol(protocol[1]);
        return selectedProtocol;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.initPipeline(ctx)) {
            ctx.pipeline().remove(this);
        }
    }

    private boolean initPipeline(ChannelHandlerContext ctx) {
        SslHandler handler = ctx.pipeline().get(SslHandler.class);
        if (handler == null) {
            throw new IllegalStateException("SslHandler is needed for SPDY");
        }
        SelectedProtocol protocol = this.getProtocol(handler.engine());
        switch (protocol) {
            case UNKNOWN: {
                return false;
            }
            case SPDY_3_1: {
                this.addSpdyHandlers(ctx, SpdyVersion.SPDY_3_1);
                break;
            }
            case HTTP_1_0: 
            case HTTP_1_1: {
                this.addHttpHandlers(ctx);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown SelectedProtocol");
            }
        }
        return true;
    }

    protected void addSpdyHandlers(ChannelHandlerContext ctx, SpdyVersion version) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("spdyFrameCodec", (ChannelHandler)new SpdyFrameCodec(version));
        pipeline.addLast("spdySessionHandler", (ChannelHandler)new SpdySessionHandler(version, true));
        pipeline.addLast("spdyHttpEncoder", (ChannelHandler)new SpdyHttpEncoder(version));
        pipeline.addLast("spdyHttpDecoder", (ChannelHandler)new SpdyHttpDecoder(version, this.maxSpdyContentLength));
        pipeline.addLast("spdyStreamIdHandler", (ChannelHandler)new SpdyHttpResponseStreamIdHandler());
        pipeline.addLast("httpRequestHandler", (ChannelHandler)this.createHttpRequestHandlerForSpdy());
    }

    protected void addHttpHandlers(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("httpRequestDecoder", (ChannelHandler)new HttpRequestDecoder());
        pipeline.addLast("httpResponseEncoder", (ChannelHandler)new HttpResponseEncoder());
        pipeline.addLast("httpChunkAggregator", (ChannelHandler)new HttpObjectAggregator(this.maxHttpContentLength));
        pipeline.addLast("httpRequestHandler", (ChannelHandler)this.createHttpRequestHandlerForHttp());
    }

    protected abstract ChannelInboundHandler createHttpRequestHandlerForHttp();

    protected ChannelInboundHandler createHttpRequestHandlerForSpdy() {
        return this.createHttpRequestHandlerForHttp();
    }

    public static enum SelectedProtocol {
        SPDY_3_1("spdy/3.1"),
        HTTP_1_1("http/1.1"),
        HTTP_1_0("http/1.0"),
        UNKNOWN("Unknown");

        private final String name;

        private SelectedProtocol(String defaultName) {
            this.name = defaultName;
        }

        public String protocolName() {
            return this.name;
        }

        public static SelectedProtocol protocol(String name) {
            for (SelectedProtocol protocol : SelectedProtocol.values()) {
                if (!protocol.protocolName().equals(name)) continue;
                return protocol;
            }
            return UNKNOWN;
        }
    }
}

