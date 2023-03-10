/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Merger;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.StyleImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class AlwaysMerger
implements Merger {
    static final AlwaysMerger INSTANCE = new AlwaysMerger();

    private AlwaysMerger() {
    }

    @Override
    public void mergeColor(StyleImpl.BuilderImpl target, @Nullable TextColor color) {
        target.color(color);
    }

    @Override
    public void mergeDecoration(StyleImpl.BuilderImpl target, @NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
        target.decoration(decoration, state);
    }

    @Override
    public void mergeClickEvent(StyleImpl.BuilderImpl target, @Nullable ClickEvent event) {
        target.clickEvent(event);
    }

    @Override
    public void mergeHoverEvent(StyleImpl.BuilderImpl target, @Nullable HoverEvent<?> event) {
        target.hoverEvent(event);
    }

    @Override
    public void mergeInsertion(StyleImpl.BuilderImpl target, @Nullable String insertion) {
        target.insertion(insertion);
    }

    @Override
    public void mergeFont(StyleImpl.BuilderImpl target, @Nullable Key font) {
        target.font(font);
    }
}

