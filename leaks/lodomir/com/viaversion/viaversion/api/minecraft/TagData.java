/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

public final class TagData {
    private final String identifier;
    private final int[] entries;

    public TagData(String identifier, int[] entries) {
        this.identifier = identifier;
        this.entries = entries;
    }

    public String identifier() {
        return this.identifier;
    }

    public int[] entries() {
        return this.entries;
    }
}

