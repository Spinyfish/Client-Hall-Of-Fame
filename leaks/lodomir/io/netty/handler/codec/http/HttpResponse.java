/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpResponse
extends HttpMessage {
    public HttpResponseStatus getStatus();

    public HttpResponse setStatus(HttpResponseStatus var1);

    @Override
    public HttpResponse setProtocolVersion(HttpVersion var1);
}

