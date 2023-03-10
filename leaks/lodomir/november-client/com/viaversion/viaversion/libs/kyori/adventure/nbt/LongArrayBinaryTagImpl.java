/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Debug$Renderer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.ArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Debug.Renderer(text="\"long[\" + this.value.length + \"]\"", childrenArray="this.value", hasChildren="this.value.length > 0")
final class LongArrayBinaryTagImpl
extends ArrayBinaryTagImpl
implements LongArrayBinaryTag {
    final long[] value;

    LongArrayBinaryTagImpl(long[] value) {
        this.value = Arrays.copyOf(value, value.length);
    }

    @Override
    public long @NotNull [] value() {
        return Arrays.copyOf(this.value, this.value.length);
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public long get(int index) {
        LongArrayBinaryTagImpl.checkIndex(index, this.value.length);
        return this.value[index];
    }

    @Override
    public  @NotNull PrimitiveIterator.OfLong iterator() {
        return new PrimitiveIterator.OfLong(){
            private int index;

            @Override
            public boolean hasNext() {
                return this.index < LongArrayBinaryTagImpl.this.value.length - 1;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return LongArrayBinaryTagImpl.this.value[this.index++];
            }
        };
    }

    @Override
    public  @NotNull Spliterator.OfLong spliterator() {
        return Arrays.spliterator(this.value);
    }

    @Override
    @NotNull
    public LongStream stream() {
        return Arrays.stream(this.value);
    }

    @Override
    public void forEachLong(@NotNull LongConsumer action) {
        int length = this.value.length;
        for (int i = 0; i < length; ++i) {
            action.accept(this.value[i]);
        }
    }

    static long[] value(LongArrayBinaryTag tag) {
        return tag instanceof LongArrayBinaryTagImpl ? ((LongArrayBinaryTagImpl)tag).value : tag.value();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        LongArrayBinaryTagImpl that = (LongArrayBinaryTagImpl)other;
        return Arrays.equals(this.value, that.value);
    }

    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.value));
    }
}

