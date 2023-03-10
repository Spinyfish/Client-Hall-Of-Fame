/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

public final class ChannelMetadata {
    private final boolean hasDisconnect;

    public ChannelMetadata(boolean hasDisconnect) {
        this.hasDisconnect = hasDisconnect;
    }

    public boolean hasDisconnect() {
        return this.hasDisconnect;
    }
}

