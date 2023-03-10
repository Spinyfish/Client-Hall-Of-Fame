/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.pointer;

import com.viaversion.viaversion.libs.kyori.adventure.pointer.Pointer;
import com.viaversion.viaversion.libs.kyori.adventure.pointer.Pointers;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PointersImpl
implements Pointers {
    static final Pointers EMPTY = new Pointers(){

        @Override
        @NotNull
        public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
            return Optional.empty();
        }

        @Override
        public <T> boolean supports(@NotNull Pointer<T> pointer) {
            return false;
        }

        @Override
        public @NotNull Pointers.Builder toBuilder() {
            return new BuilderImpl();
        }

        public String toString() {
            return "EmptyPointers";
        }
    };
    private final Map<Pointer<?>, Supplier<?>> pointers;

    PointersImpl(@NotNull BuilderImpl builder) {
        this.pointers = new HashMap(builder.pointers);
    }

    @Override
    @NotNull
    public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
        Objects.requireNonNull(pointer, "pointer");
        Supplier<?> supplier = this.pointers.get(pointer);
        if (supplier == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(supplier.get());
    }

    @Override
    public <T> boolean supports(@NotNull Pointer<T> pointer) {
        Objects.requireNonNull(pointer, "pointer");
        return this.pointers.containsKey(pointer);
    }

    @Override
    @NotNull
    public Pointers.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    implements Pointers.Builder {
        private final Map<Pointer<?>, Supplier<?>> pointers;

        BuilderImpl() {
            this.pointers = new HashMap();
        }

        BuilderImpl(@NotNull PointersImpl pointers) {
            this.pointers = new HashMap(pointers.pointers);
        }

        @Override
        @NotNull
        public <T> Pointers.Builder withDynamic(@NotNull Pointer<T> pointer, @NotNull Supplier<@Nullable T> value) {
            this.pointers.put(Objects.requireNonNull(pointer, "pointer"), Objects.requireNonNull(value, "value"));
            return this;
        }

        @Override
        @NotNull
        public Pointers build() {
            return new PointersImpl(this);
        }
    }
}

