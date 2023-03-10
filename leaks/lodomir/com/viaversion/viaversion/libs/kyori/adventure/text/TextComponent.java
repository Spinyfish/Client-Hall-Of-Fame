/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.JoinConfiguration;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScopedComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TextComponent
extends BuildableComponent<TextComponent, Builder>,
ScopedComponent<TextComponent> {
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @NotNull
    public static TextComponent ofChildren(ComponentLike ... components) {
        Component joined = Component.join(JoinConfiguration.noSeparators(), components);
        if (joined instanceof TextComponent) {
            return (TextComponent)joined;
        }
        return (TextComponent)((Builder)Component.text().append(joined)).build();
    }

    @NotNull
    public String content();

    @Contract(pure=true)
    @NotNull
    public TextComponent content(@NotNull String var1);

    public static interface Builder
    extends ComponentBuilder<TextComponent, Builder> {
        @NotNull
        public String content();

        @Contract(value="_ -> this")
        @NotNull
        public Builder content(@NotNull String var1);
    }
}

