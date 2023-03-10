/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.cors;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class CorsHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
    private final CorsConfig config;
    private HttpRequest request;

    public CorsHandler(CorsConfig config) {
        this.config = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.config.isCorsSupportEnabled() && msg instanceof HttpRequest) {
            this.request = (HttpRequest)msg;
            if (CorsHandler.isPreflightRequest(this.request)) {
                this.handlePreflight(ctx, this.request);
                return;
            }
            if (this.config.isShortCurcuit() && !this.validateOrigin()) {
                CorsHandler.forbidden(ctx, this.request);
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        if (this.setOrigin(response)) {
            this.setAllowMethods(response);
            this.setAllowHeaders(response);
            this.setAllowCredentials(response);
            this.setMaxAge(response);
            this.setPreflightHeaders(response);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void setPreflightHeaders(HttpResponse response) {
        response.headers().add(this.config.preflightResponseHeaders());
    }

    private boolean setOrigin(HttpResponse response) {
        String origin = this.request.headers().get("Origin");
        if (origin != null) {
            if ("null".equals(origin) && this.config.isNullOriginAllowed()) {
                CorsHandler.setAnyOrigin(response);
                return true;
            }
            if (this.config.isAnyOriginSupported()) {
                if (this.config.isCredentialsAllowed()) {
                    this.echoRequestOrigin(response);
                    CorsHandler.setVaryHeader(response);
                } else {
                    CorsHandler.setAnyOrigin(response);
                }
                return true;
            }
            if (this.config.origins().contains(origin)) {
                CorsHandler.setOrigin(response, origin);
                CorsHandler.setVaryHeader(response);
                return true;
            }
            logger.debug("Request origin [" + origin + "] was not among the configured origins " + this.config.origins());
        }
        return false;
    }

    private boolean validateOrigin() {
        if (this.config.isAnyOriginSupported()) {
            return true;
        }
        String origin = this.request.headers().get("Origin");
        if (origin == null) {
            return true;
        }
        if ("null".equals(origin) && this.config.isNullOriginAllowed()) {
            return true;
        }
        return this.config.origins().contains(origin);
    }

    private void echoRequestOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, this.request.headers().get("Origin"));
    }

    private static void setVaryHeader(HttpResponse response) {
        response.headers().set("Vary", (Object)"Origin");
    }

    private static void setAnyOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, "*");
    }

    private static void setOrigin(HttpResponse response, String origin) {
        response.headers().set("Access-Control-Allow-Origin", (Object)origin);
    }

    private void setAllowCredentials(HttpResponse response) {
        if (this.config.isCredentialsAllowed()) {
            response.headers().set("Access-Control-Allow-Credentials", (Object)"true");
        }
    }

    private static boolean isPreflightRequest(HttpRequest request) {
        HttpHeaders headers = request.headers();
        return request.getMethod().equals(HttpMethod.OPTIONS) && headers.contains("Origin") && headers.contains("Access-Control-Request-Method");
    }

    private void setExposeHeaders(HttpResponse response) {
        if (!this.config.exposedHeaders().isEmpty()) {
            response.headers().set("Access-Control-Expose-Headers", (Iterable<?>)this.config.exposedHeaders());
        }
    }

    private void setAllowMethods(HttpResponse response) {
        response.headers().set("Access-Control-Allow-Methods", (Iterable<?>)this.config.allowedRequestMethods());
    }

    private void setAllowHeaders(HttpResponse response) {
        response.headers().set("Access-Control-Allow-Headers", (Iterable<?>)this.config.allowedRequestHeaders());
    }

    private void setMaxAge(HttpResponse response) {
        response.headers().set("Access-Control-Max-Age", (Object)this.config.maxAge());
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HttpResponse response;
        if (this.config.isCorsSupportEnabled() && msg instanceof HttpResponse && this.setOrigin(response = (HttpResponse)msg)) {
            this.setAllowCredentials(response);
            this.setAllowHeaders(response);
            this.setExposeHeaders(response);
        }
        ctx.writeAndFlush(msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Caught error in CorsHandler", cause);
        ctx.fireExceptionCaught(cause);
    }

    private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
    }
}

