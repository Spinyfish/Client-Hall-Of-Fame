/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets;

public class ServerProtocolVersionSingleton
implements ServerProtocolVersion {
    private final int protocolVersion;

    public ServerProtocolVersionSingleton(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public int lowestSupportedVersion() {
        return this.protocolVersion;
    }

    @Override
    public int highestSupportedVersion() {
        return this.protocolVersion;
    }

    @Override
    public IntSortedSet supportedVersions() {
        return IntSortedSets.singleton(this.protocolVersion);
    }
}

