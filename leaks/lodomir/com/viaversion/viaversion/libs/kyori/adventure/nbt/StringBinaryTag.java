/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.StringBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface StringBinaryTag
extends BinaryTag {
    @NotNull
    public static StringBinaryTag of(@NotNull String value) {
        return new StringBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<StringBinaryTag> type() {
        return BinaryTagTypes.STRING;
    }

    @NotNull
    public String value();
}

