/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.protocol;

public interface ProtocolPathKey {
    public int clientProtocolVersion();

    public int serverProtocolVersion();

    @Deprecated
    default public int getClientProtocolVersion() {
        return this.clientProtocolVersion();
    }

    @Deprecated
    default public int getServerProtocolVersion() {
        return this.serverProtocolVersion();
    }
}

