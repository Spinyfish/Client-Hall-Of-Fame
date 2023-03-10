/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.examination;

import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.Examiner;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public interface Examinable {
    @NotNull
    default public String examinableName() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.empty();
    }

    @NotNull
    default public <R> R examine(@NotNull Examiner<R> examiner) {
        return examiner.examine(this);
    }
}

