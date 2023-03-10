/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.viaversion.viaversion.api.protocol.version;

import com.google.common.base.Preconditions;

public class VersionRange {
    private final String baseVersion;
    private final int rangeFrom;
    private final int rangeTo;

    public VersionRange(String baseVersion, int rangeFrom, int rangeTo) {
        Preconditions.checkNotNull((Object)baseVersion);
        Preconditions.checkArgument((rangeFrom >= 0 ? 1 : 0) != 0);
        Preconditions.checkArgument((rangeTo > rangeFrom ? 1 : 0) != 0);
        this.baseVersion = baseVersion;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }

    public String baseVersion() {
        return this.baseVersion;
    }

    public int rangeFrom() {
        return this.rangeFrom;
    }

    public int rangeTo() {
        return this.rangeTo;
    }

    @Deprecated
    public String getBaseVersion() {
        return this.baseVersion;
    }

    @Deprecated
    public int getRangeFrom() {
        return this.rangeFrom;
    }

    @Deprecated
    public int getRangeTo() {
        return this.rangeTo;
    }
}

