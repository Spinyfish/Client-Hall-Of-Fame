/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.sound;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.key.Keyed;
import com.viaversion.viaversion.libs.kyori.adventure.sound.SoundImpl;
import com.viaversion.viaversion.libs.kyori.adventure.sound.SoundStop;
import com.viaversion.viaversion.libs.kyori.adventure.util.Index;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface Sound
extends Examinable {
    @NotNull
    public static Sound sound(final @NotNull Key name, @NotNull Source source, float volume, float pitch) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        return new SoundImpl(source, volume, pitch){

            @Override
            @NotNull
            public Key name() {
                return name;
            }
        };
    }

    @NotNull
    public static Sound sound(@NotNull Type type, @NotNull Source source, float volume, float pitch) {
        Objects.requireNonNull(type, "type");
        return Sound.sound(type.key(), source, volume, pitch);
    }

    @NotNull
    public static Sound sound(final @NotNull Supplier<? extends Type> type, @NotNull Source source, float volume, float pitch) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(source, "source");
        return new SoundImpl(source, volume, pitch){

            @Override
            @NotNull
            public Key name() {
                return ((Type)type.get()).key();
            }
        };
    }

    @NotNull
    public static Sound sound(@NotNull Key name, @NotNull Source.Provider source, float volume, float pitch) {
        return Sound.sound(name, source.soundSource(), volume, pitch);
    }

    @NotNull
    public static Sound sound(@NotNull Type type, @NotNull Source.Provider source, float volume, float pitch) {
        return Sound.sound(type, source.soundSource(), volume, pitch);
    }

    @NotNull
    public static Sound sound(@NotNull Supplier<? extends Type> type, @NotNull Source.Provider source, float volume, float pitch) {
        return Sound.sound(type, source.soundSource(), volume, pitch);
    }

    @NotNull
    public Key name();

    @NotNull
    public Source source();

    public float volume();

    public float pitch();

    @NotNull
    public SoundStop asStop();

    public static interface Emitter {
        @NotNull
        public static Emitter self() {
            return SoundImpl.EMITTER_SELF;
        }
    }

    public static interface Type
    extends Keyed {
        @Override
        @NotNull
        public Key key();
    }

    public static enum Source {
        MASTER("master"),
        MUSIC("music"),
        RECORD("record"),
        WEATHER("weather"),
        BLOCK("block"),
        HOSTILE("hostile"),
        NEUTRAL("neutral"),
        PLAYER("player"),
        AMBIENT("ambient"),
        VOICE("voice");

        public static final Index<String, Source> NAMES;
        private final String name;

        private Source(String name) {
            this.name = name;
        }

        static {
            NAMES = Index.create(Source.class, source -> source.name);
        }

        public static interface Provider {
            @NotNull
            public Source soundSource();
        }
    }
}

