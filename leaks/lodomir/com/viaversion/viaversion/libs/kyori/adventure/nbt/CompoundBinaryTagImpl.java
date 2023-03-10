/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Debug$Renderer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.AbstractBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ListBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.StringBinaryTag;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Debug.Renderer(text="\"CompoundBinaryTag[length=\" + this.tags.size() + \"]\"", childrenArray="this.tags.entrySet().toArray()", hasChildren="!this.tags.isEmpty()")
final class CompoundBinaryTagImpl
extends AbstractBinaryTag
implements CompoundBinaryTag {
    static final CompoundBinaryTag EMPTY = new CompoundBinaryTagImpl(Collections.emptyMap());
    private final Map<String, BinaryTag> tags;
    private final int hashCode;

    CompoundBinaryTagImpl(Map<String, BinaryTag> tags) {
        this.tags = Collections.unmodifiableMap(tags);
        this.hashCode = tags.hashCode();
    }

    public boolean contains(@NotNull String key, @NotNull BinaryTagType<?> type) {
        @Nullable BinaryTag tag = this.tags.get(key);
        return tag != null && type.test(tag.type());
    }

    @Override
    @NotNull
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.tags.keySet());
    }

    @Override
    @Nullable
    public BinaryTag get(String key) {
        return this.tags.get(key);
    }

    @Override
    @NotNull
    public CompoundBinaryTag put(@NotNull String key, @NotNull BinaryTag tag) {
        return this.edit(map -> map.put(key, tag));
    }

    @Override
    @NotNull
    public CompoundBinaryTag put(@NotNull CompoundBinaryTag tag) {
        return this.edit(map -> {
            for (String key : tag.keySet()) {
                map.put(key, tag.get(key));
            }
        });
    }

    @Override
    @NotNull
    public CompoundBinaryTag put(@NotNull Map<String, ? extends BinaryTag> tags) {
        return this.edit(map -> map.putAll(tags));
    }

    @Override
    @NotNull
    public CompoundBinaryTag remove(@NotNull String key, @Nullable Consumer<? super BinaryTag> removed) {
        if (!this.tags.containsKey(key)) {
            return this;
        }
        return this.edit(map -> {
            BinaryTag tag = (BinaryTag)map.remove(key);
            if (removed != null) {
                removed.accept(tag);
            }
        });
    }

    @Override
    public byte getByte(@NotNull String key, byte defaultValue) {
        if (this.contains(key, BinaryTagTypes.BYTE)) {
            return ((NumberBinaryTag)this.tags.get(key)).byteValue();
        }
        return defaultValue;
    }

    @Override
    public short getShort(@NotNull String key, short defaultValue) {
        if (this.contains(key, BinaryTagTypes.SHORT)) {
            return ((NumberBinaryTag)this.tags.get(key)).shortValue();
        }
        return defaultValue;
    }

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        if (this.contains(key, BinaryTagTypes.INT)) {
            return ((NumberBinaryTag)this.tags.get(key)).intValue();
        }
        return defaultValue;
    }

    @Override
    public long getLong(@NotNull String key, long defaultValue) {
        if (this.contains(key, BinaryTagTypes.LONG)) {
            return ((NumberBinaryTag)this.tags.get(key)).longValue();
        }
        return defaultValue;
    }

    @Override
    public float getFloat(@NotNull String key, float defaultValue) {
        if (this.contains(key, BinaryTagTypes.FLOAT)) {
            return ((NumberBinaryTag)this.tags.get(key)).floatValue();
        }
        return defaultValue;
    }

    @Override
    public double getDouble(@NotNull String key, double defaultValue) {
        if (this.contains(key, BinaryTagTypes.DOUBLE)) {
            return ((NumberBinaryTag)this.tags.get(key)).doubleValue();
        }
        return defaultValue;
    }

    @Override
    public byte @NotNull [] getByteArray(@NotNull String key) {
        if (this.contains(key, BinaryTagTypes.BYTE_ARRAY)) {
            return ((ByteArrayBinaryTag)this.tags.get(key)).value();
        }
        return new byte[0];
    }

    @Override
    public byte @NotNull [] getByteArray(@NotNull String key, byte @NotNull [] defaultValue) {
        if (this.contains(key, BinaryTagTypes.BYTE_ARRAY)) {
            return ((ByteArrayBinaryTag)this.tags.get(key)).value();
        }
        return defaultValue;
    }

    @Override
    @NotNull
    public String getString(@NotNull String key, @NotNull String defaultValue) {
        if (this.contains(key, BinaryTagTypes.STRING)) {
            return ((StringBinaryTag)this.tags.get(key)).value();
        }
        return defaultValue;
    }

    @Override
    @NotNull
    public ListBinaryTag getList(@NotNull String key, @NotNull ListBinaryTag defaultValue) {
        if (this.contains(key, BinaryTagTypes.LIST)) {
            return (ListBinaryTag)this.tags.get(key);
        }
        return defaultValue;
    }

    @Override
    @NotNull
    public ListBinaryTag getList(@NotNull String key, @NotNull BinaryTagType<? extends BinaryTag> expectedType, @NotNull ListBinaryTag defaultValue) {
        ListBinaryTag tag;
        if (this.contains(key, BinaryTagTypes.LIST) && expectedType.test((tag = (ListBinaryTag)this.tags.get(key)).elementType())) {
            return tag;
        }
        return defaultValue;
    }

    @Override
    @NotNull
    public CompoundBinaryTag getCompound(@NotNull String key, @NotNull CompoundBinaryTag defaultValue) {
        if (this.contains(key, BinaryTagTypes.COMPOUND)) {
            return (CompoundBinaryTag)this.tags.get(key);
        }
        return defaultValue;
    }

    @Override
    public int @NotNull [] getIntArray(@NotNull String key) {
        if (this.contains(key, BinaryTagTypes.INT_ARRAY)) {
            return ((IntArrayBinaryTag)this.tags.get(key)).value();
        }
        return new int[0];
    }

    @Override
    public int @NotNull [] getIntArray(@NotNull String key, int @NotNull [] defaultValue) {
        if (this.contains(key, BinaryTagTypes.INT_ARRAY)) {
            return ((IntArrayBinaryTag)this.tags.get(key)).value();
        }
        return defaultValue;
    }

    @Override
    public long @NotNull [] getLongArray(@NotNull String key) {
        if (this.contains(key, BinaryTagTypes.LONG_ARRAY)) {
            return ((LongArrayBinaryTag)this.tags.get(key)).value();
        }
        return new long[0];
    }

    @Override
    public long @NotNull [] getLongArray(@NotNull String key, long @NotNull [] defaultValue) {
        if (this.contains(key, BinaryTagTypes.LONG_ARRAY)) {
            return ((LongArrayBinaryTag)this.tags.get(key)).value();
        }
        return defaultValue;
    }

    private CompoundBinaryTag edit(Consumer<Map<String, BinaryTag>> consumer) {
        HashMap<String, BinaryTag> tags = new HashMap<String, BinaryTag>(this.tags);
        consumer.accept(tags);
        return new CompoundBinaryTagImpl(tags);
    }

    public boolean equals(Object that) {
        return this == that || that instanceof CompoundBinaryTagImpl && this.tags.equals(((CompoundBinaryTagImpl)that).tags);
    }

    public int hashCode() {
        return this.hashCode;
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("tags", this.tags));
    }

    @Override
    @NotNull
    public Iterator<Map.Entry<String, ? extends BinaryTag>> iterator() {
        return this.tags.entrySet().iterator();
    }

    @Override
    public void forEach(@NotNull Consumer<? super Map.Entry<String, ? extends BinaryTag>> action) {
        this.tags.entrySet().forEach(Objects.requireNonNull(action, "action"));
    }
}

