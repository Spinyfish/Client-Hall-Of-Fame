/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagLike;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;

public interface BinaryTag
extends BinaryTagLike,
Examinable {
    @NotNull
    public BinaryTagType<? extends BinaryTag> type();

    @Override
    @NotNull
    default public BinaryTag asBinaryTag() {
        return this;
    }
}

