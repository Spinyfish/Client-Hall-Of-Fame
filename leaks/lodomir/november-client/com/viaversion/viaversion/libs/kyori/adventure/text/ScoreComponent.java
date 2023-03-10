/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScopedComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScoreComponent
extends BuildableComponent<ScoreComponent, Builder>,
ScopedComponent<ScoreComponent> {
    @NotNull
    public String name();

    @Contract(pure=true)
    @NotNull
    public ScoreComponent name(@NotNull String var1);

    @NotNull
    public String objective();

    @Contract(pure=true)
    @NotNull
    public ScoreComponent objective(@NotNull String var1);

    @Deprecated
    @Nullable
    public String value();

    @Deprecated
    @Contract(pure=true)
    @NotNull
    public ScoreComponent value(@Nullable String var1);

    public static interface Builder
    extends ComponentBuilder<ScoreComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder name(@NotNull String var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder objective(@NotNull String var1);

        @Deprecated
        @Contract(value="_ -> this")
        @NotNull
        public Builder value(@Nullable String var1);
    }
}

