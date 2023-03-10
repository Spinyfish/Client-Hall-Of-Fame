/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;

abstract class AbstractBinaryTag
implements BinaryTag {
    AbstractBinaryTag() {
    }

    @Override
    @NotNull
    public final String examinableName() {
        return this.type().toString();
    }

    public final String toString() {
        return this.examine(StringExaminer.simpleEscaping());
    }
}

