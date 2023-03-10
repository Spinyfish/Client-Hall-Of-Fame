/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;

public final class SpdyHttpHeaders {
    private SpdyHttpHeaders() {
    }

    public static void removeStreamId(HttpMessage message) {
        message.headers().remove("X-SPDY-Stream-ID");
    }

    public static int getStreamId(HttpMessage message) {
        return HttpHeaders.getIntHeader(message, "X-SPDY-Stream-ID");
    }

    public static void setStreamId(HttpMessage message, int streamId) {
        HttpHeaders.setIntHeader(message, "X-SPDY-Stream-ID", streamId);
    }

    public static void removeAssociatedToStreamId(HttpMessage message) {
        message.headers().remove("X-SPDY-Associated-To-Stream-ID");
    }

    public static int getAssociatedToStreamId(HttpMessage message) {
        return HttpHeaders.getIntHeader(message, "X-SPDY-Associated-To-Stream-ID", 0);
    }

    public static void setAssociatedToStreamId(HttpMessage message, int associatedToStreamId) {
        HttpHeaders.setIntHeader(message, "X-SPDY-Associated-To-Stream-ID", associatedToStreamId);
    }

    public static void removePriority(HttpMessage message) {
        message.headers().remove("X-SPDY-Priority");
    }

    public static byte getPriority(HttpMessage message) {
        return (byte)HttpHeaders.getIntHeader(message, "X-SPDY-Priority", 0);
    }

    public static void setPriority(HttpMessage message, byte priority) {
        HttpHeaders.setIntHeader(message, "X-SPDY-Priority", (int)priority);
    }

    public static void removeUrl(HttpMessage message) {
        message.headers().remove("X-SPDY-URL");
    }

    public static String getUrl(HttpMessage message) {
        return message.headers().get("X-SPDY-URL");
    }

    public static void setUrl(HttpMessage message, String url) {
        message.headers().set("X-SPDY-URL", (Object)url);
    }

    public static void removeScheme(HttpMessage message) {
        message.headers().remove("X-SPDY-Scheme");
    }

    public static String getScheme(HttpMessage message) {
        return message.headers().get("X-SPDY-Scheme");
    }

    public static void setScheme(HttpMessage message, String scheme) {
        message.headers().set("X-SPDY-Scheme", (Object)scheme);
    }

    public static final class Names {
        public static final String STREAM_ID = "X-SPDY-Stream-ID";
        public static final String ASSOCIATED_TO_STREAM_ID = "X-SPDY-Associated-To-Stream-ID";
        public static final String PRIORITY = "X-SPDY-Priority";
        public static final String URL = "X-SPDY-URL";
        public static final String SCHEME = "X-SPDY-Scheme";

        private Names() {
        }
    }
}

