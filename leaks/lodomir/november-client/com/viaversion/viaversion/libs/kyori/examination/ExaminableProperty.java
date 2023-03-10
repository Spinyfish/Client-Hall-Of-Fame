/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.examination;

import com.viaversion.viaversion.libs.kyori.examination.Examiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ExaminableProperty {
    private ExaminableProperty() {
    }

    @NotNull
    public abstract String name();

    @NotNull
    public abstract <R> R examine(@NotNull Examiner<? extends R> var1);

    public String toString() {
        return "ExaminableProperty{" + this.name() + "}";
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final @Nullable Object value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final @Nullable String value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final boolean value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final boolean[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final byte value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final byte[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final char value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final char[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final double value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final double[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final float value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final float[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final int value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final int[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final long value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final long[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final short value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }

    @NotNull
    public static ExaminableProperty of(final @NotNull String name, final short[] value) {
        return new ExaminableProperty(){

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public <R> R examine(@NotNull Examiner<? extends R> examiner) {
                return examiner.examine(value);
            }
        };
    }
}

