/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.item;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Item {
    public int identifier();

    public void setIdentifier(int var1);

    public int amount();

    public void setAmount(int var1);

    default public short data() {
        return 0;
    }

    default public void setData(short data) {
        throw new UnsupportedOperationException();
    }

    public @Nullable CompoundTag tag();

    public void setTag(@Nullable CompoundTag var1);
}

