/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ListBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ListBinaryTagImpl;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ListTagBuilder<T extends BinaryTag>
implements ListBinaryTag.Builder<T> {
    @Nullable
    private List<BinaryTag> tags;
    private BinaryTagType<? extends BinaryTag> elementType;

    ListTagBuilder() {
        this(BinaryTagTypes.END);
    }

    ListTagBuilder(BinaryTagType<? extends BinaryTag> type) {
        this.elementType = type;
    }

    @Override
    public @NotNull ListBinaryTag.Builder<T> add(BinaryTag tag) {
        ListBinaryTagImpl.noAddEnd(tag);
        if (this.elementType == BinaryTagTypes.END) {
            this.elementType = tag.type();
        }
        ListBinaryTagImpl.mustBeSameType(tag, this.elementType);
        if (this.tags == null) {
            this.tags = new ArrayList<BinaryTag>();
        }
        this.tags.add(tag);
        return this;
    }

    @Override
    public @NotNull ListBinaryTag.Builder<T> add(Iterable<? extends T> tagsToAdd) {
        for (BinaryTag tag : tagsToAdd) {
            this.add(tag);
        }
        return this;
    }

    @Override
    @NotNull
    public ListBinaryTag build() {
        if (this.tags == null) {
            return ListBinaryTag.empty();
        }
        return new ListBinaryTagImpl(this.elementType, new ArrayList<BinaryTag>(this.tags));
    }
}

