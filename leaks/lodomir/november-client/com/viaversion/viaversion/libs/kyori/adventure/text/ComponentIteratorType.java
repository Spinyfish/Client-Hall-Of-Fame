/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentIteratorFlag;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@ApiStatus.NonExtendable
public interface ComponentIteratorType {
    public static final ComponentIteratorType DEPTH_FIRST = (component, deque, flags) -> {
        HoverEvent<?> hoverEvent;
        int i;
        if (flags.contains((Object)ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent)component;
            List<Component> args = translatable.args();
            for (i = args.size() - 1; i >= 0; --i) {
                deque.addFirst(args.get(i));
            }
        }
        if ((hoverEvent = component.hoverEvent()) != null) {
            HoverEvent.Action<?> action = hoverEvent.action();
            if (flags.contains((Object)ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
                deque.addFirst(((HoverEvent.ShowEntity)hoverEvent.value()).name());
            } else if (flags.contains((Object)ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
                deque.addFirst((Component)hoverEvent.value());
            }
        }
        List<Component> children = component.children();
        for (i = children.size() - 1; i >= 0; --i) {
            deque.addFirst(children.get(i));
        }
    };
    public static final ComponentIteratorType BREADTH_FIRST = (component, deque, flags) -> {
        HoverEvent<?> hoverEvent;
        if (flags.contains((Object)ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
            deque.addAll(((TranslatableComponent)component).args());
        }
        if ((hoverEvent = component.hoverEvent()) != null) {
            HoverEvent.Action<?> action = hoverEvent.action();
            if (flags.contains((Object)ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
                deque.addLast(((HoverEvent.ShowEntity)hoverEvent.value()).name());
            } else if (flags.contains((Object)ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
                deque.addLast((Component)hoverEvent.value());
            }
        }
        deque.addAll(component.children());
    };

    public void populate(@NotNull Component var1, @NotNull Deque<Component> var2, @NotNull Set<ComponentIteratorFlag> var3);
}

