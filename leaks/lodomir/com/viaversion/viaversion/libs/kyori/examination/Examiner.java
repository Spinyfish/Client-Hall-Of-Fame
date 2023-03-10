/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.examination;

import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Examiner<R> {
    @NotNull
    default public R examine(@NotNull Examinable examinable) {
        return this.examine(examinable.examinableName(), examinable.examinableProperties());
    }

    @NotNull
    public R examine(@NotNull String var1, @NotNull Stream<? extends ExaminableProperty> var2);

    @NotNull
    public R examine(@Nullable Object var1);

    @NotNull
    public R examine(boolean var1);

    @NotNull
    public R examine(boolean @Nullable [] var1);

    @NotNull
    public R examine(byte var1);

    @NotNull
    public R examine(byte @Nullable [] var1);

    @NotNull
    public R examine(char var1);

    @NotNull
    public R examine(char @Nullable [] var1);

    @NotNull
    public R examine(double var1);

    @NotNull
    public R examine(double @Nullable [] var1);

    @NotNull
    public R examine(float var1);

    @NotNull
    public R examine(float @Nullable [] var1);

    @NotNull
    public R examine(int var1);

    @NotNull
    public R examine(int @Nullable [] var1);

    @NotNull
    public R examine(long var1);

    @NotNull
    public R examine(long @Nullable [] var1);

    @NotNull
    public R examine(short var1);

    @NotNull
    public R examine(short @Nullable [] var1);

    @NotNull
    public R examine(@Nullable String var1);
}

