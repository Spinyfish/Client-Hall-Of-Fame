/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTagImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CompoundTagBuilder
implements CompoundBinaryTag.Builder {
    @Nullable
    private Map<String, BinaryTag> tags;

    CompoundTagBuilder() {
    }

    private Map<String, BinaryTag> tags() {
        if (this.tags == null) {
            this.tags = new HashMap<String, BinaryTag>();
        }
        return this.tags;
    }

    @Override
    public @NotNull CompoundBinaryTag.Builder put(@NotNull String key, @NotNull BinaryTag tag) {
        this.tags().put(key, tag);
        return this;
    }

    @Override
    public @NotNull CompoundBinaryTag.Builder put(@NotNull CompoundBinaryTag tag) {
        Map<String, BinaryTag> tags = this.tags();
        for (String key : tag.keySet()) {
            tags.put(key, tag.get(key));
        }
        return this;
    }

    @Override
    public @NotNull CompoundBinaryTag.Builder put(@NotNull Map<String, ? extends BinaryTag> tags) {
        this.tags().putAll(tags);
        return this;
    }

    @Override
    public @NotNull CompoundBinaryTag.Builder remove(@NotNull String key, @Nullable Consumer<? super BinaryTag> removed) {
        if (this.tags != null) {
            BinaryTag tag = this.tags.remove(key);
            if (removed != null) {
                removed.accept(tag);
            }
        }
        return this;
    }

    @Override
    @NotNull
    public CompoundBinaryTag build() {
        if (this.tags == null) {
            return CompoundBinaryTag.empty();
        }
        return new CompoundBinaryTagImpl(new HashMap<String, BinaryTag>(this.tags));
    }
}

