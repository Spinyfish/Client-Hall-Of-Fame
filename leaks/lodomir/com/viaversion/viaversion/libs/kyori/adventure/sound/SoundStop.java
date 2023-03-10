/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.sound;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.sound.Sound;
import com.viaversion.viaversion.libs.kyori.adventure.sound.SoundStopImpl;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface SoundStop
extends Examinable {
    @NotNull
    public static SoundStop all() {
        return SoundStopImpl.ALL;
    }

    @NotNull
    public static SoundStop named(final @NotNull Key sound) {
        Objects.requireNonNull(sound, "sound");
        return new SoundStopImpl(null){

            @Override
            @NotNull
            public Key sound() {
                return sound;
            }
        };
    }

    @NotNull
    public static SoundStop named(final @NotNull Sound.Type sound) {
        Objects.requireNonNull(sound, "sound");
        return new SoundStopImpl(null){

            @Override
            @NotNull
            public Key sound() {
                return sound.key();
            }
        };
    }

    @NotNull
    public static SoundStop named(final @NotNull Supplier<? extends Sound.Type> sound) {
        Objects.requireNonNull(sound, "sound");
        return new SoundStopImpl(null){

            @Override
            @NotNull
            public Key sound() {
                return ((Sound.Type)sound.get()).key();
            }
        };
    }

    @NotNull
    public static SoundStop source(@NotNull Sound.Source source) {
        Objects.requireNonNull(source, "source");
        return new SoundStopImpl(source){

            @Override
            @Nullable
            public Key sound() {
                return null;
            }
        };
    }

    @NotNull
    public static SoundStop namedOnSource(final @NotNull Key sound, @NotNull Sound.Source source) {
        Objects.requireNonNull(sound, "sound");
        Objects.requireNonNull(source, "source");
        return new SoundStopImpl(source){

            @Override
            @NotNull
            public Key sound() {
                return sound;
            }
        };
    }

    @NotNull
    public static SoundStop namedOnSource(@NotNull Sound.Type sound, @NotNull Sound.Source source) {
        Objects.requireNonNull(sound, "sound");
        return SoundStop.namedOnSource(sound.key(), source);
    }

    @NotNull
    public static SoundStop namedOnSource(final @NotNull Supplier<? extends Sound.Type> sound, @NotNull Sound.Source source) {
        Objects.requireNonNull(sound, "sound");
        Objects.requireNonNull(source, "source");
        return new SoundStopImpl(source){

            @Override
            @NotNull
            public Key sound() {
                return ((Sound.Type)sound.get()).key();
            }
        };
    }

    @Nullable
    public Key sound();

    public @Nullable Sound.Source source();
}

