/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilderApplicable;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
extends Buildable.Builder<C>,
ComponentBuilderApplicable,
ComponentLike {
    @Contract(value="_ -> this")
    @NotNull
    public B append(@NotNull Component var1);

    @Contract(value="_ -> this")
    @NotNull
    default public B append(@NotNull ComponentLike component) {
        return this.append(component.asComponent());
    }

    @Contract(value="_ -> this")
    @NotNull
    default public B append(@NotNull ComponentBuilder<?, ?> builder) {
        return this.append((Component)builder.build());
    }

    @Contract(value="_ -> this")
    @NotNull
    public B append(Component ... var1);

    @Contract(value="_ -> this")
    @NotNull
    public B append(ComponentLike ... var1);

    @Contract(value="_ -> this")
    @NotNull
    public B append(@NotNull Iterable<? extends ComponentLike> var1);

    @Contract(value="_ -> this")
    @NotNull
    default public B apply(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
        consumer.accept(this);
        return (B)this;
    }

    @Contract(value="_ -> this")
    @NotNull
    public B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> var1);

    @Contract(value="_ -> this")
    @NotNull
    public B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> var1);

    @Contract(value="_ -> this")
    @NotNull
    public B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> var1);

    @NotNull
    public List<Component> children();

    @Contract(value="_ -> this")
    @NotNull
    public B style(@NotNull Style var1);

    @Contract(value="_ -> this")
    @NotNull
    public B style(@NotNull Consumer<Style.Builder> var1);

    @Contract(value="_ -> this")
    @NotNull
    public B font(@Nullable Key var1);

    @Contract(value="_ -> this")
    @NotNull
    public B color(@Nullable TextColor var1);

    @Contract(value="_ -> this")
    @NotNull
    public B colorIfAbsent(@Nullable TextColor var1);

    @Contract(value="_, _ -> this")
    @NotNull
    default public B decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
        TextDecoration.State state = TextDecoration.State.byBoolean(flag);
        decorations.forEach(decoration -> this.decoration((TextDecoration)decoration, state));
        return (B)this;
    }

    @Contract(value="_ -> this")
    @NotNull
    default public B decorate(@NotNull TextDecoration decoration) {
        return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    @Contract(value="_ -> this")
    @NotNull
    default public B decorate(TextDecoration ... decorations) {
        int length = decorations.length;
        for (int i = 0; i < length; ++i) {
            this.decorate(decorations[i]);
        }
        return (B)this;
    }

    @Contract(value="_, _ -> this")
    @NotNull
    default public B decoration(@NotNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    @Contract(value="_, _ -> this")
    @NotNull
    public B decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

    @Contract(value="_ -> this")
    @NotNull
    public B clickEvent(@Nullable ClickEvent var1);

    @Contract(value="_ -> this")
    @NotNull
    public B hoverEvent(@Nullable HoverEventSource<?> var1);

    @Contract(value="_ -> this")
    @NotNull
    public B insertion(@Nullable String var1);

    @Contract(value="_ -> this")
    @NotNull
    default public B mergeStyle(@NotNull Component that) {
        return this.mergeStyle(that, Style.Merge.all());
    }

    @Contract(value="_, _ -> this")
    @NotNull
    default public B mergeStyle(@NotNull Component that, Style.Merge ... merges) {
        return this.mergeStyle(that, Style.Merge.of(merges));
    }

    @Contract(value="_, _ -> this")
    @NotNull
    public B mergeStyle(@NotNull Component var1, @NotNull Set<Style.Merge> var2);

    @NotNull
    public B resetStyle();

    @Override
    @NotNull
    public C build();

    @Contract(value="_ -> this")
    @NotNull
    default public B applicableApply(@NotNull ComponentBuilderApplicable applicable) {
        applicable.componentBuilderApply(this);
        return (B)this;
    }

    @Override
    default public void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
        component.append(this);
    }

    @Override
    @NotNull
    default public Component asComponent() {
        return this.build();
    }
}

