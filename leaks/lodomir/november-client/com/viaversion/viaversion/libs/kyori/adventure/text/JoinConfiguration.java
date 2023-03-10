/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.JoinConfigurationImpl;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface JoinConfiguration
extends Buildable<JoinConfiguration, Builder>,
Examinable {
    @NotNull
    public static Builder builder() {
        return new JoinConfigurationImpl.BuilderImpl();
    }

    @NotNull
    public static JoinConfiguration noSeparators() {
        return JoinConfigurationImpl.NULL;
    }

    @NotNull
    public static JoinConfiguration separator(@Nullable ComponentLike separator) {
        if (separator == null) {
            return JoinConfigurationImpl.NULL;
        }
        return (JoinConfiguration)JoinConfiguration.builder().separator(separator).build();
    }

    @NotNull
    public static JoinConfiguration separators(@Nullable ComponentLike separator, @Nullable ComponentLike lastSeparator) {
        if (separator == null && lastSeparator == null) {
            return JoinConfigurationImpl.NULL;
        }
        return (JoinConfiguration)JoinConfiguration.builder().separator(separator).lastSeparator(lastSeparator).build();
    }

    @Nullable
    public Component prefix();

    @Nullable
    public Component suffix();

    @Nullable
    public Component separator();

    @Nullable
    public Component lastSeparator();

    @Nullable
    public Component lastSeparatorIfSerial();

    @NotNull
    public Function<ComponentLike, Component> convertor();

    @NotNull
    public Predicate<ComponentLike> predicate();

    public static interface Builder
    extends Buildable.Builder<JoinConfiguration> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder prefix(@Nullable ComponentLike var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder suffix(@Nullable ComponentLike var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder separator(@Nullable ComponentLike var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder lastSeparator(@Nullable ComponentLike var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder lastSeparatorIfSerial(@Nullable ComponentLike var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder convertor(@NotNull Function<ComponentLike, Component> var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder predicate(@NotNull Predicate<ComponentLike> var1);
    }
}

