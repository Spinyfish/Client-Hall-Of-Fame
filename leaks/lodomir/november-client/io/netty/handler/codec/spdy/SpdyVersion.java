/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

public enum SpdyVersion {
    SPDY_3_1(3, 1);

    private final int version;
    private final int minorVersion;

    private SpdyVersion(int version, int minorVersion) {
        this.version = version;
        this.minorVersion = minorVersion;
    }

    int getVersion() {
        return this.version;
    }

    int getMinorVersion() {
        return this.minorVersion;
    }
}

