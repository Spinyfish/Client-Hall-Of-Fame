/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public interface FullHttpResponse
extends HttpResponse,
FullHttpMessage {
    @Override
    public FullHttpResponse copy();

    @Override
    public FullHttpResponse retain(int var1);

    @Override
    public FullHttpResponse retain();

    @Override
    public FullHttpResponse setProtocolVersion(HttpVersion var1);

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus var1);
}

