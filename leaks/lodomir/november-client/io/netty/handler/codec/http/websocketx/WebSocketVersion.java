/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

public enum WebSocketVersion {
    UNKNOWN,
    V00,
    V07,
    V08,
    V13;


    public String toHttpHeaderValue() {
        if (this == V00) {
            return "0";
        }
        if (this == V07) {
            return "7";
        }
        if (this == V08) {
            return "8";
        }
        if (this == V13) {
            return "13";
        }
        throw new IllegalStateException("Unknown web socket version: " + (Object)((Object)this));
    }
}

