/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.DoubleBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.NotNull;

public interface DoubleBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static DoubleBinaryTag of(double value) {
        return new DoubleBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<DoubleBinaryTag> type() {
        return BinaryTagTypes.DOUBLE;
    }

    public double value();
}

