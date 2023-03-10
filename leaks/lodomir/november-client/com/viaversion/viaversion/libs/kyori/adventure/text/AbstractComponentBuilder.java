/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
implements ComponentBuilder<C, B> {
    protected List<Component> children = Collections.emptyList();
    @Nullable
    private Style style;
    private @Nullable Style.Builder styleBuilder;

    protected AbstractComponentBuilder() {
    }

    protected AbstractComponentBuilder(@NotNull C component) {
        List<Component> children = component.children();
        if (!children.isEmpty()) {
            this.children = new ArrayList<Component>(children);
        }
        if (component.hasStyling()) {
            this.style = component.style();
        }
    }

    @Override
    @NotNull
    public B append(@NotNull Component component) {
        if (component == Component.empty()) {
            return (B)this;
        }
        this.prepareChildren();
        this.children.add(Objects.requireNonNull(component, "component"));
        return (B)this;
    }

    @Override
    @NotNull
    public B append(Component ... components) {
        Objects.requireNonNull(components, "components");
        boolean prepared = false;
        for (Component component : components) {
            if (component == Component.empty()) continue;
            if (!prepared) {
                this.prepareChildren();
                prepared = true;
            }
            this.children.add(Objects.requireNonNull(component, "components[?]"));
        }
        return (B)this;
    }

    @Override
    @NotNull
    public B append(ComponentLike ... components) {
        Objects.requireNonNull(components, "components");
        boolean prepared = false;
        int length = components.length;
        for (int i = 0; i < length; ++i) {
            Component component = components[i].asComponent();
            if (component == Component.empty()) continue;
            if (!prepared) {
                this.prepareChildren();
                prepared = true;
            }
            this.children.add(Objects.requireNonNull(component, "components[?]"));
        }
        return (B)this;
    }

    @Override
    @NotNull
    public B append(@NotNull Iterable<? extends ComponentLike> components) {
        Objects.requireNonNull(components, "components");
        boolean prepared = false;
        for (ComponentLike componentLike : components) {
            Component component = componentLike.asComponent();
            if (component == Component.empty()) continue;
            if (!prepared) {
                this.prepareChildren();
                prepared = true;
            }
            this.children.add(Objects.requireNonNull(component, "components[?]"));
        }
        return (B)this;
    }

    private void prepareChildren() {
        if (this.children == Collections.emptyList()) {
            this.children = new ArrayList<Component>();
        }
    }

    @Override
    @NotNull
    public B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
        this.apply(consumer);
        if (this.children == Collections.emptyList()) {
            return (B)this;
        }
        ListIterator<Component> it = this.children.listIterator();
        while (it.hasNext()) {
            Component child = it.next();
            if (!(child instanceof BuildableComponent)) continue;
            Buildable.Builder childBuilder = ((BuildableComponent)child).toBuilder();
            childBuilder.applyDeep(consumer);
            it.set((Component)childBuilder.build());
        }
        return (B)this;
    }

    @Override
    @NotNull
    public B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
        if (this.children == Collections.emptyList()) {
            return (B)this;
        }
        ListIterator<Component> it = this.children.listIterator();
        while (it.hasNext()) {
            BuildableComponent<?, ?> mappedChild;
            Component child = it.next();
            if (!(child instanceof BuildableComponent) || child == (mappedChild = Objects.requireNonNull(function.apply((BuildableComponent)child), "mappedChild"))) continue;
            it.set(mappedChild);
        }
        return (B)this;
    }

    @Override
    @NotNull
    public B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
        if (this.children == Collections.emptyList()) {
            return (B)this;
        }
        ListIterator<Component> it = this.children.listIterator();
        while (it.hasNext()) {
            Component child = it.next();
            if (!(child instanceof BuildableComponent)) continue;
            BuildableComponent<?, ?> mappedChild = Objects.requireNonNull(function.apply((BuildableComponent)child), "mappedChild");
            if (mappedChild.children().isEmpty()) {
                if (child == mappedChild) continue;
                it.set(mappedChild);
                continue;
            }
            Buildable.Builder builder = mappedChild.toBuilder();
            builder.mapChildrenDeep(function);
            it.set((Component)builder.build());
        }
        return (B)this;
    }

    @Override
    @NotNull
    public List<Component> children() {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    @NotNull
    public B style(@NotNull Style style) {
        this.style = style;
        this.styleBuilder = null;
        return (B)this;
    }

    @Override
    @NotNull
    public B style(@NotNull Consumer<Style.Builder> consumer) {
        consumer.accept(this.styleBuilder());
        return (B)this;
    }

    @Override
    @NotNull
    public B font(@Nullable Key font) {
        this.styleBuilder().font(font);
        return (B)this;
    }

    @Override
    @NotNull
    public B color(@Nullable TextColor color) {
        this.styleBuilder().color(color);
        return (B)this;
    }

    @Override
    @NotNull
    public B colorIfAbsent(@Nullable TextColor color) {
        this.styleBuilder().colorIfAbsent(color);
        return (B)this;
    }

    @Override
    @NotNull
    public B decoration(@NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
        this.styleBuilder().decoration(decoration, state);
        return (B)this;
    }

    @Override
    @NotNull
    public B clickEvent(@Nullable ClickEvent event) {
        this.styleBuilder().clickEvent(event);
        return (B)this;
    }

    @Override
    @NotNull
    public B hoverEvent(@Nullable HoverEventSource<?> source) {
        this.styleBuilder().hoverEvent(source);
        return (B)this;
    }

    @Override
    @NotNull
    public B insertion(@Nullable String insertion) {
        this.styleBuilder().insertion(insertion);
        return (B)this;
    }

    @Override
    @NotNull
    public B mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
        this.styleBuilder().merge(that.style(), merges);
        return (B)this;
    }

    @Override
    @NotNull
    public B resetStyle() {
        this.style = null;
        this.styleBuilder = null;
        return (B)this;
    }

    private @NotNull Style.Builder styleBuilder() {
        if (this.styleBuilder == null) {
            if (this.style != null) {
                this.styleBuilder = this.style.toBuilder();
                this.style = null;
            } else {
                this.styleBuilder = Style.style();
            }
        }
        return this.styleBuilder;
    }

    protected final boolean hasStyle() {
        return this.styleBuilder != null || this.style != null;
    }

    @NotNull
    protected Style buildStyle() {
        if (this.styleBuilder != null) {
            return this.styleBuilder.build();
        }
        if (this.style != null) {
            return this.style;
        }
        return Style.empty();
    }
}

