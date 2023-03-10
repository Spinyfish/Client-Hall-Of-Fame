/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.NotNull;

public interface LongBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static LongBinaryTag of(long value) {
        return new LongBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<LongBinaryTag> type() {
        return BinaryTagTypes.LONG;
    }

    public long value();
}

