/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import org.jetbrains.annotations.NotNull;

public interface Codec<D, E, DX extends Throwable, EX extends Throwable> {
    @NotNull
    public static <D, E, DX extends Throwable, EX extends Throwable> Codec<D, E, DX, EX> of(final @NotNull Decoder<D, E, DX> decoder, final @NotNull Encoder<D, E, EX> encoder) {
        return new Codec<D, E, DX, EX>(){

            @Override
            @NotNull
            public D decode(@NotNull E encoded) throws Throwable {
                return decoder.decode(encoded);
            }

            @Override
            @NotNull
            public E encode(@NotNull D decoded) throws Throwable {
                return encoder.encode(decoded);
            }
        };
    }

    @NotNull
    public D decode(@NotNull E var1) throws DX;

    @NotNull
    public E encode(@NotNull D var1) throws EX;

    public static interface Encoder<D, E, X extends Throwable> {
        @NotNull
        public E encode(@NotNull D var1) throws X;
    }

    public static interface Decoder<D, E, X extends Throwable> {
        @NotNull
        public D decode(@NotNull E var1) throws X;
    }
}

