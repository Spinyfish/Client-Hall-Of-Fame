/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnknownNullability
 */
package com.viaversion.viaversion.libs.kyori.adventure.pointer;

import com.viaversion.viaversion.libs.kyori.adventure.pointer.Pointer;
import com.viaversion.viaversion.libs.kyori.adventure.pointer.PointersImpl;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface Pointers
extends Buildable<Pointers, Builder> {
    @Contract(pure=true)
    @NotNull
    public static Pointers empty() {
        return PointersImpl.EMPTY;
    }

    @Contract(pure=true)
    @NotNull
    public static Builder builder() {
        return new PointersImpl.BuilderImpl();
    }

    @NotNull
    public <T> Optional<T> get(@NotNull Pointer<T> var1);

    @Contract(value="_, null -> _; _, !null -> !null")
    @Nullable
    default public <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
        return this.get(pointer).orElse(defaultValue);
    }

    default public <T> @UnknownNullability T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
        return this.get(pointer).orElseGet(defaultValue);
    }

    public <T> boolean supports(@NotNull Pointer<T> var1);

    public static interface Builder
    extends Buildable.Builder<Pointers> {
        @Contract(value="_, _ -> this")
        @NotNull
        default public <T> Builder withStatic(@NotNull Pointer<T> pointer, @Nullable T value) {
            return this.withDynamic(pointer, () -> value);
        }

        @Contract(value="_, _ -> this")
        @NotNull
        public <T> Builder withDynamic(@NotNull Pointer<T> var1, @NotNull Supplier<@Nullable T> var2);
    }
}

