/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpRequest
extends HttpMessage {
    public HttpMethod getMethod();

    public HttpRequest setMethod(HttpMethod var1);

    public String getUri();

    public HttpRequest setUri(String var1);

    @Override
    public HttpRequest setProtocolVersion(HttpVersion var1);
}

