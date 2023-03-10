/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Buildable<R, B extends Builder<R>> {
    @Contract(mutates="param1")
    @NotNull
    public static <R extends Buildable<R, B>, B extends Builder<R>> R configureAndBuild(@NotNull B builder, @Nullable Consumer<? super B> consumer) {
        if (consumer != null) {
            consumer.accept(builder);
        }
        return (R)((Buildable)builder.build());
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public B toBuilder();

    public static interface Builder<R> {
        @Contract(value="-> new", pure=true)
        @NotNull
        public R build();
    }
}

