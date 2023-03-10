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

final class IfAbsentOnTargetMerger
implements Merger {
    static final IfAbsentOnTargetMerger INSTANCE = new IfAbsentOnTargetMerger();

    private IfAbsentOnTargetMerger() {
    }

    @Override
    public void mergeColor(StyleImpl.BuilderImpl target, @Nullable TextColor color) {
        if (target.color == null) {
            target.color(color);
        }
    }

    @Override
    public void mergeDecoration(StyleImpl.BuilderImpl target, @NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
        target.decorationIfAbsent(decoration, state);
    }

    @Override
    public void mergeClickEvent(StyleImpl.BuilderImpl target, @Nullable ClickEvent event) {
        if (target.clickEvent == null) {
            target.clickEvent(event);
        }
    }

    @Override
    public void mergeHoverEvent(StyleImpl.BuilderImpl target, @Nullable HoverEvent<?> event) {
        if (target.hoverEvent == null) {
            target.hoverEvent(event);
        }
    }

    @Override
    public void mergeInsertion(StyleImpl.BuilderImpl target, @Nullable String insertion) {
        if (target.insertion == null) {
            target.insertion(insertion);
        }
    }

    @Override
    public void mergeFont(StyleImpl.BuilderImpl target, @Nullable Key font) {
        if (target.font == null) {
            target.font(font);
        }
    }
}

