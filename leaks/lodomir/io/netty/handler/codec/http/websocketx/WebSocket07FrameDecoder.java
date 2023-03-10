/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;

public class WebSocket07FrameDecoder
extends WebSocket08FrameDecoder {
    public WebSocket07FrameDecoder(boolean maskedPayload, boolean allowExtensions, int maxFramePayloadLength) {
        super(maskedPayload, allowExtensions, maxFramePayloadLength);
    }
}

