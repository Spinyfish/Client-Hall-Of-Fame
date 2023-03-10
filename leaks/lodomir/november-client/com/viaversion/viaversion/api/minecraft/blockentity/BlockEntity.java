/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.blockentity;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface BlockEntity {
    default public byte sectionX() {
        return (byte)(this.packedXZ() >> 4 & 0xF);
    }

    default public byte sectionZ() {
        return (byte)(this.packedXZ() & 0xF);
    }

    public byte packedXZ();

    public short y();

    public int typeId();

    public @Nullable CompoundTag tag();
}

