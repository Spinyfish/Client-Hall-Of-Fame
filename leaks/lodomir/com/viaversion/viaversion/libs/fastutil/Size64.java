/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

import java.util.Collection;
import java.util.Map;

public interface Size64 {
    public long size64();

    @Deprecated
    default public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size64());
    }

    public static long sizeOf(Collection<?> c) {
        return c instanceof Size64 ? ((Size64)((Object)c)).size64() : (long)c.size();
    }

    public static long sizeOf(Map<?, ?> m) {
        return m instanceof Size64 ? ((Size64)((Object)m)).size64() : (long)m.size();
    }
}

