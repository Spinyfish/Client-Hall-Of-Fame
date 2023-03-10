/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public class DefaultLastHttpContent
extends DefaultHttpContent
implements LastHttpContent {
    private final HttpHeaders trailingHeaders;
    private final boolean validateHeaders;

    public DefaultLastHttpContent() {
        this(Unpooled.buffer(0));
    }

    public DefaultLastHttpContent(ByteBuf content) {
        this(content, true);
    }

    public DefaultLastHttpContent(ByteBuf content, boolean validateHeaders) {
        super(content);
        this.trailingHeaders = new TrailingHeaders(validateHeaders);
        this.validateHeaders = validateHeaders;
    }

    @Override
    public LastHttpContent copy() {
        DefaultLastHttpContent copy = new DefaultLastHttpContent(this.content().copy(), this.validateHeaders);
        copy.trailingHeaders().set(this.trailingHeaders());
        return copy;
    }

    @Override
    public LastHttpContent duplicate() {
        DefaultLastHttpContent copy = new DefaultLastHttpContent(this.content().duplicate(), this.validateHeaders);
        copy.trailingHeaders().set(this.trailingHeaders());
        return copy;
    }

    @Override
    public LastHttpContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public LastHttpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    private void appendHeaders(StringBuilder buf) {
        for (Map.Entry e : this.trailingHeaders()) {
            buf.append((String)e.getKey());
            buf.append(": ");
            buf.append((String)e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }

    private static final class TrailingHeaders
    extends DefaultHttpHeaders {
        TrailingHeaders(boolean validate) {
            super(validate);
        }

        @Override
        void validateHeaderName0(CharSequence name) {
            super.validateHeaderName0(name);
            if (HttpHeaders.equalsIgnoreCase("Content-Length", name) || HttpHeaders.equalsIgnoreCase("Transfer-Encoding", name) || HttpHeaders.equalsIgnoreCase("Trailer", name)) {
                throw new IllegalArgumentException("prohibited trailing header: " + name);
            }
        }
    }
}

