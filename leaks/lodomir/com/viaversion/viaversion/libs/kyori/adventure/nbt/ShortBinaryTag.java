/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ShortBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface ShortBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static ShortBinaryTag of(short value) {
        return new ShortBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<ShortBinaryTag> type() {
        return BinaryTagTypes.SHORT;
    }

    public short value();
}

