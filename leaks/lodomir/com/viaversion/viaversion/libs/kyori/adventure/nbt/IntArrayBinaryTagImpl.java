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
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Debug.Renderer(text="\"int[\" + this.value.length + \"]\"", childrenArray="this.value", hasChildren="this.value.length > 0")
final class IntArrayBinaryTagImpl
extends ArrayBinaryTagImpl
implements IntArrayBinaryTag {
    final int[] value;

    IntArrayBinaryTagImpl(int ... value) {
        this.value = Arrays.copyOf(value, value.length);
    }

    @Override
    public int @NotNull [] value() {
        return Arrays.copyOf(this.value, this.value.length);
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public int get(int index) {
        IntArrayBinaryTagImpl.checkIndex(index, this.value.length);
        return this.value[index];
    }

    @Override
    public  @NotNull PrimitiveIterator.OfInt iterator() {
        return new PrimitiveIterator.OfInt(){
            private int index;

            @Override
            public boolean hasNext() {
                return this.index < IntArrayBinaryTagImpl.this.value.length - 1;
            }

            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return IntArrayBinaryTagImpl.this.value[this.index++];
            }
        };
    }

    @Override
    public  @NotNull Spliterator.OfInt spliterator() {
        return Arrays.spliterator(this.value);
    }

    @Override
    @NotNull
    public IntStream stream() {
        return Arrays.stream(this.value);
    }

    @Override
    public void forEachInt(@NotNull IntConsumer action) {
        int length = this.value.length;
        for (int i = 0; i < length; ++i) {
            action.accept(this.value[i]);
        }
    }

    static int[] value(IntArrayBinaryTag tag) {
        return tag instanceof IntArrayBinaryTagImpl ? ((IntArrayBinaryTagImpl)tag).value : tag.value();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        IntArrayBinaryTagImpl that = (IntArrayBinaryTagImpl)other;
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

