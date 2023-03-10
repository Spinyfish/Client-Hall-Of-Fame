/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentCompaction {
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();

    private ComponentCompaction() {
    }

    static Component compact(@NotNull Component self, @Nullable Style parentStyle) {
        int i;
        TextComponent textComponent;
        int childrenSize;
        List<Component> children = self.children();
        Component optimized = self.children(Collections.emptyList());
        if (parentStyle != null) {
            optimized = optimized.style(ComponentCompaction.simplifyStyle(self.style(), parentStyle));
        }
        if ((childrenSize = children.size()) == 0) {
            return optimized;
        }
        if (childrenSize == 1 && self instanceof TextComponent && (textComponent = (TextComponent)self).content().isEmpty()) {
            Component child = children.get(0);
            return child.style(child.style().merge(optimized.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).compact();
        }
        Style childParentStyle = optimized.style();
        if (parentStyle != null) {
            childParentStyle = childParentStyle.merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        }
        ArrayList<Component> childrenToAppend = new ArrayList<Component>(children.size());
        for (i = 0; i < children.size(); ++i) {
            childrenToAppend.add(ComponentCompaction.compact(children.get(i), childParentStyle));
        }
        while (!childrenToAppend.isEmpty()) {
            Component child = (Component)childrenToAppend.get(0);
            Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
            if (!(optimized instanceof TextComponent) || !(child instanceof TextComponent) || !Objects.equals(childStyle, childParentStyle)) break;
            optimized = ComponentCompaction.joinText((TextComponent)optimized, (TextComponent)child);
            childrenToAppend.remove(0);
            childrenToAppend.addAll(0, child.children());
        }
        i = 0;
        while (i + 1 < childrenToAppend.size()) {
            Component child = (Component)childrenToAppend.get(i);
            Component neighbor = (Component)childrenToAppend.get(i + 1);
            Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
            Style neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
            if (child.children().isEmpty() && child instanceof TextComponent && neighbor instanceof TextComponent && childStyle.equals(neighborStyle)) {
                TextComponent combined = ComponentCompaction.joinText((TextComponent)child, (TextComponent)neighbor);
                childrenToAppend.set(i, combined);
                childrenToAppend.remove(i + 1);
                continue;
            }
            ++i;
        }
        return optimized.children(childrenToAppend);
    }

    @NotNull
    private static Style simplifyStyle(@NotNull Style style, @NotNull Style parentStyle) {
        if (style.isEmpty()) {
            return style;
        }
        Style.Builder builder = style.toBuilder();
        if (Objects.equals(style.font(), parentStyle.font())) {
            builder.font(null);
        }
        if (Objects.equals(style.color(), parentStyle.color())) {
            builder.color(null);
        }
        for (TextDecoration decoration : DECORATIONS) {
            if (style.decoration(decoration) != parentStyle.decoration(decoration)) continue;
            builder.decoration(decoration, TextDecoration.State.NOT_SET);
        }
        if (Objects.equals(style.clickEvent(), parentStyle.clickEvent())) {
            builder.clickEvent(null);
        }
        if (Objects.equals(style.hoverEvent(), parentStyle.hoverEvent())) {
            builder.hoverEvent(null);
        }
        if (Objects.equals(style.insertion(), parentStyle.insertion())) {
            builder.insertion(null);
        }
        return builder.build();
    }

    private static TextComponent joinText(TextComponent one, TextComponent two) {
        return new TextComponentImpl(two.children(), one.style(), one.content() + two.content());
    }
}

