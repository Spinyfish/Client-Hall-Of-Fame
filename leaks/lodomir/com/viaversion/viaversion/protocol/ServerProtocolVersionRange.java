/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;

public class ServerProtocolVersionRange
implements ServerProtocolVersion {
    private final int lowestSupportedVersion;
    private final int highestSupportedVersion;
    private final IntSortedSet supportedVersions;

    public ServerProtocolVersionRange(int lowestSupportedVersion, int highestSupportedVersion, IntSortedSet supportedVersions) {
        this.lowestSupportedVersion = lowestSupportedVersion;
        this.highestSupportedVersion = highestSupportedVersion;
        this.supportedVersions = supportedVersions;
    }

    @Override
    public int lowestSupportedVersion() {
        return this.lowestSupportedVersion;
    }

    @Override
    public int highestSupportedVersion() {
        return this.highestSupportedVersion;
    }

    @Override
    public IntSortedSet supportedVersions() {
        return this.supportedVersions;
    }
}

