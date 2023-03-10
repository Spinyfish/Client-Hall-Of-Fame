/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentSerializer<I extends Component, O extends Component, R> {
    @NotNull
    public O deserialize(@NotNull R var1);

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(value="!null -> !null; null -> null", pure=true)
    @Nullable
    default public O deseializeOrNull(@Nullable R input) {
        return this.deserializeOrNull(input);
    }

    @Contract(value="!null -> !null; null -> null", pure=true)
    @Nullable
    default public O deserializeOrNull(@Nullable R input) {
        return this.deserializeOr(input, null);
    }

    @Contract(value="!null, _ -> !null; null, _ -> param2", pure=true)
    @Nullable
    default public O deserializeOr(@Nullable R input, @Nullable O fallback) {
        if (input == null) {
            return fallback;
        }
        return this.deserialize(input);
    }

    @NotNull
    public R serialize(@NotNull I var1);

    @Contract(value="!null -> !null; null -> null", pure=true)
    @Nullable
    default public R serializeOrNull(@Nullable I component) {
        return this.serializeOr(component, null);
    }

    @Contract(value="!null, _ -> !null; null, _ -> param2", pure=true)
    @Nullable
    default public R serializeOr(@Nullable I component, @Nullable R fallback) {
        if (component == null) {
            return fallback;
        }
        return this.serialize(component);
    }
}

