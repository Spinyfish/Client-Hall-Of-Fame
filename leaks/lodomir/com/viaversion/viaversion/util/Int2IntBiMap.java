/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.viaversion.viaversion.util;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Int2IntBiMap
extends Int2IntMap {
    public Int2IntBiMap inverse();

    @Override
    public int put(int var1, int var2);

    @Override
    @Deprecated
    default public void putAll(@NonNull Map<? extends Integer, ? extends Integer> m) {
        throw new UnsupportedOperationException();
    }
}

