/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilderApplicable;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentIterator;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentIteratorFlag;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentIteratorType;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.JoinConfiguration;
import com.viaversion.viaversion.libs.kyori.adventure.text.JoinConfigurationImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.PatternReplacementResult;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextReplacementConfig;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translatable;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.adventure.util.ForwardingIterator;
import com.viaversion.viaversion.libs.kyori.adventure.util.IntFunction2;
import com.viaversion.viaversion.libs.kyori.adventure.util.MonkeyBars;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.NonExtendable
public interface Component
extends ComponentBuilderApplicable,
ComponentLike,
Examinable,
HoverEventSource<Component> {
    public static final BiPredicate<? super Component, ? super Component> EQUALS = Objects::equals;
    public static final BiPredicate<? super Component, ? super Component> EQUALS_IDENTITY = (a, b) -> a == b;

    @NotNull
    public static TextComponent empty() {
        return TextComponentImpl.EMPTY;
    }

    @NotNull
    public static TextComponent newline() {
        return TextComponentImpl.NEWLINE;
    }

    @NotNull
    public static TextComponent space() {
        return TextComponentImpl.SPACE;
    }

    @Deprecated
    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent join(@NotNull ComponentLike separator, ComponentLike ... components) {
        return Component.join(separator, Arrays.asList(components));
    }

    @Deprecated
    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent join(@NotNull ComponentLike separator, Iterable<? extends ComponentLike> components) {
        Component component = Component.join(JoinConfiguration.separator(separator), components);
        if (component instanceof TextComponent) {
            return (TextComponent)component;
        }
        return (TextComponent)((TextComponent.Builder)Component.text().append(component)).build();
    }

    @Contract(pure=true)
    @NotNull
    public static Component join(@NotNull JoinConfiguration config, ComponentLike ... components) {
        return Component.join(config, Arrays.asList(components));
    }

    @Contract(pure=true)
    @NotNull
    public static Component join(@NotNull JoinConfiguration config, @NotNull Iterable<? extends ComponentLike> components) {
        return JoinConfigurationImpl.join(config, components);
    }

    @NotNull
    public static Collector<Component, ? extends ComponentBuilder<?, ?>, Component> toComponent() {
        return Component.toComponent(Component.empty());
    }

    @NotNull
    public static Collector<Component, ? extends ComponentBuilder<?, ?>, Component> toComponent(@NotNull Component separator) {
        return Collector.of(Component::text, (builder, add) -> {
            if (separator != Component.empty() && !builder.children().isEmpty()) {
                builder.append(separator);
            }
            builder.append((Component)add);
        }, (a, b) -> {
            List<Component> aChildren = a.children();
            TextComponent.Builder ret = (TextComponent.Builder)Component.text().append(aChildren);
            if (!aChildren.isEmpty()) {
                ret.append(separator);
            }
            ret.append(b.children());
            return ret;
        }, ComponentBuilder::build, new Collector.Characteristics[0]);
    }

    @Contract(pure=true)
    public static @NotNull BlockNBTComponent.Builder blockNBT() {
        return new BlockNBTComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static BlockNBTComponent blockNBT(@NotNull Consumer<? super BlockNBTComponent.Builder> consumer) {
        return (BlockNBTComponent)Buildable.configureAndBuild(Component.blockNBT(), consumer);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static BlockNBTComponent blockNBT(@NotNull String nbtPath, @NotNull BlockNBTComponent.Pos pos) {
        return Component.blockNBT(nbtPath, false, pos);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static BlockNBTComponent blockNBT(@NotNull String nbtPath, boolean interpret, @NotNull BlockNBTComponent.Pos pos) {
        return Component.blockNBT(nbtPath, interpret, null, pos);
    }

    @Contract(value="_, _, _, _ -> new", pure=true)
    @NotNull
    public static BlockNBTComponent blockNBT(@NotNull String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull BlockNBTComponent.Pos pos) {
        return new BlockNBTComponentImpl(Collections.emptyList(), Style.empty(), nbtPath, interpret, separator, pos);
    }

    @Contract(pure=true)
    public static @NotNull EntityNBTComponent.Builder entityNBT() {
        return new EntityNBTComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static EntityNBTComponent entityNBT(@NotNull Consumer<? super EntityNBTComponent.Builder> consumer) {
        return (EntityNBTComponent)Buildable.configureAndBuild(Component.entityNBT(), consumer);
    }

    @Contract(value="_, _ -> new")
    @NotNull
    public static EntityNBTComponent entityNBT(@NotNull String nbtPath, @NotNull String selector) {
        return (EntityNBTComponent)((EntityNBTComponent.Builder)Component.entityNBT().nbtPath(nbtPath)).selector(selector).build();
    }

    @Contract(pure=true)
    public static @NotNull KeybindComponent.Builder keybind() {
        return new KeybindComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static KeybindComponent keybind(@NotNull Consumer<? super KeybindComponent.Builder> consumer) {
        return (KeybindComponent)Buildable.configureAndBuild(Component.keybind(), consumer);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull String keybind) {
        return Component.keybind(keybind, Style.empty());
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull KeybindComponent.KeybindLike keybind) {
        return Component.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind(), Style.empty());
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull String keybind, @NotNull Style style) {
        return new KeybindComponentImpl(Collections.emptyList(), style, keybind);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull KeybindComponent.KeybindLike keybind, @NotNull Style style) {
        return new KeybindComponentImpl(Collections.emptyList(), style, Objects.requireNonNull(keybind, "keybind").asKeybind());
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color) {
        return Component.keybind(keybind, Style.style(color));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull KeybindComponent.KeybindLike keybind, @Nullable TextColor color) {
        return Component.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind(), Style.style(color));
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.keybind(keybind, Style.style(color, decorations));
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull KeybindComponent.KeybindLike keybind, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind(), Style.style(color, decorations));
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.keybind(keybind, Style.style(color, decorations));
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static KeybindComponent keybind(@NotNull KeybindComponent.KeybindLike keybind, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind(), Style.style(color, decorations));
    }

    @Contract(pure=true)
    public static @NotNull ScoreComponent.Builder score() {
        return new ScoreComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static ScoreComponent score(@NotNull Consumer<? super ScoreComponent.Builder> consumer) {
        return (ScoreComponent)Buildable.configureAndBuild(Component.score(), consumer);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static ScoreComponent score(@NotNull String name, @NotNull String objective) {
        return Component.score(name, objective, null);
    }

    @Deprecated
    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static ScoreComponent score(@NotNull String name, @NotNull String objective, @Nullable String value) {
        return new ScoreComponentImpl(Collections.emptyList(), Style.empty(), name, objective, value);
    }

    @Contract(pure=true)
    public static @NotNull SelectorComponent.Builder selector() {
        return new SelectorComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static SelectorComponent selector(@NotNull Consumer<? super SelectorComponent.Builder> consumer) {
        return (SelectorComponent)Buildable.configureAndBuild(Component.selector(), consumer);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static SelectorComponent selector(@NotNull String pattern) {
        return Component.selector(pattern, null);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static SelectorComponent selector(@NotNull String pattern, @Nullable ComponentLike separator) {
        return new SelectorComponentImpl(Collections.emptyList(), Style.empty(), pattern, separator);
    }

    @Contract(pure=true)
    public static @NotNull StorageNBTComponent.Builder storageNBT() {
        return new StorageNBTComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static StorageNBTComponent storageNBT(@NotNull Consumer<? super StorageNBTComponent.Builder> consumer) {
        return (StorageNBTComponent)Buildable.configureAndBuild(Component.storageNBT(), consumer);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static StorageNBTComponent storageNBT(@NotNull String nbtPath, @NotNull Key storage) {
        return Component.storageNBT(nbtPath, false, storage);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static StorageNBTComponent storageNBT(@NotNull String nbtPath, boolean interpret, @NotNull Key storage) {
        return Component.storageNBT(nbtPath, interpret, null, storage);
    }

    @Contract(value="_, _, _, _ -> new", pure=true)
    @NotNull
    public static StorageNBTComponent storageNBT(@NotNull String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull Key storage) {
        return new StorageNBTComponentImpl(Collections.emptyList(), Style.empty(), nbtPath, interpret, separator, storage);
    }

    @Contract(pure=true)
    public static @NotNull TextComponent.Builder text() {
        return new TextComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static TextComponent text(@NotNull Consumer<? super TextComponent.Builder> consumer) {
        return (TextComponent)Buildable.configureAndBuild(Component.text(), consumer);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TextComponent text(@NotNull String content) {
        if (content.isEmpty()) {
            return Component.empty();
        }
        return new TextComponentImpl(Collections.emptyList(), Style.empty(), content);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(@NotNull String content, @NotNull Style style) {
        return new TextComponentImpl(Collections.emptyList(), style, content);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(@NotNull String content, @Nullable TextColor color) {
        return new TextComponentImpl(Collections.emptyList(), Style.style(color), content);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(@NotNull String content, @Nullable TextColor color, TextDecoration ... decorations) {
        return new TextComponentImpl(Collections.emptyList(), Style.style(color, decorations), content);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(@NotNull String content, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return new TextComponentImpl(Collections.emptyList(), Style.style(color, decorations), content);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TextComponent text(boolean value) {
        return Component.text(String.valueOf(value));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(boolean value, @NotNull Style style) {
        return Component.text(String.valueOf(value), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(boolean value, @Nullable TextColor color) {
        return Component.text(String.valueOf(value), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(boolean value, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(boolean value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(pure=true)
    @NotNull
    public static TextComponent text(char value) {
        if (value == '\n') {
            return Component.newline();
        }
        if (value == ' ') {
            return Component.space();
        }
        return Component.text(String.valueOf(value));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(char value, @NotNull Style style) {
        return Component.text(String.valueOf(value), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(char value, @Nullable TextColor color) {
        return Component.text(String.valueOf(value), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(char value, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(char value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TextComponent text(double value) {
        return Component.text(String.valueOf(value));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(double value, @NotNull Style style) {
        return Component.text(String.valueOf(value), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(double value, @Nullable TextColor color) {
        return Component.text(String.valueOf(value), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(double value, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(double value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TextComponent text(float value) {
        return Component.text(String.valueOf(value));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(float value, @NotNull Style style) {
        return Component.text(String.valueOf(value), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(float value, @Nullable TextColor color) {
        return Component.text(String.valueOf(value), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(float value, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(float value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TextComponent text(int value) {
        return Component.text(String.valueOf(value));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(int value, @NotNull Style style) {
        return Component.text(String.valueOf(value), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(int value, @Nullable TextColor color) {
        return Component.text(String.valueOf(value), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(int value, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(int value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TextComponent text(long value) {
        return Component.text(String.valueOf(value));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(long value, @NotNull Style style) {
        return Component.text(String.valueOf(value), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(long value, @Nullable TextColor color) {
        return Component.text(String.valueOf(value), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(long value, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TextComponent text(long value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.text(String.valueOf(value), color, decorations);
    }

    @Contract(pure=true)
    public static @NotNull TranslatableComponent.Builder translatable() {
        return new TranslatableComponentImpl.BuilderImpl();
    }

    @Contract(value="_ -> new")
    @NotNull
    public static TranslatableComponent translatable(@NotNull Consumer<? super TranslatableComponent.Builder> consumer) {
        return (TranslatableComponent)Buildable.configureAndBuild(Component.translatable(), consumer);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key) {
        return Component.translatable(key, Style.empty());
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), Style.empty());
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @NotNull Style style) {
        return new TranslatableComponentImpl(Collections.emptyList(), style, key, Collections.emptyList());
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), style);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color) {
        return Component.translatable(key, Style.style(color));
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.translatable(key, Style.style(color, decorations));
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, TextDecoration ... decorations) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color, decorations);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.translatable(key, Style.style(color, decorations));
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color, decorations);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, ComponentLike ... args) {
        return Component.translatable(key, Style.empty(), args);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, ComponentLike ... args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @NotNull Style style, ComponentLike ... args) {
        return new TranslatableComponentImpl(Collections.emptyList(), style, key, args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style, ComponentLike ... args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), style, args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, ComponentLike ... args) {
        return Component.translatable(key, Style.style(color), args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, ComponentLike ... args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color, args);
    }

    @Contract(value="_, _, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, ComponentLike ... args) {
        return Component.translatable(key, Style.style(color, decorations), args);
    }

    @Contract(value="_, _, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, ComponentLike ... args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color, decorations, args);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @NotNull List<? extends ComponentLike> args) {
        return new TranslatableComponentImpl(Collections.emptyList(), Style.empty(), key, args);
    }

    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull List<? extends ComponentLike> args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
        return new TranslatableComponentImpl(Collections.emptyList(), style, key, args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), style, args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull List<? extends ComponentLike> args) {
        return Component.translatable(key, Style.style(color), args);
    }

    @Contract(value="_, _, _ -> new", pure=true)
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull List<? extends ComponentLike> args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color, args);
    }

    @Contract(value="_, _, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull List<? extends ComponentLike> args) {
        return Component.translatable(key, Style.style(color, decorations), args);
    }

    @Contract(value="_, _, _, _ -> new", pure=true)
    @NotNull
    public static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull List<? extends ComponentLike> args) {
        return Component.translatable(Objects.requireNonNull(translatable, "translatable").translationKey(), color, decorations, args);
    }

    public @Unmodifiable @NotNull List<Component> children();

    @Contract(pure=true)
    @NotNull
    public Component children(@NotNull List<? extends ComponentLike> var1);

    default public boolean contains(@NotNull Component that) {
        return this.contains(that, EQUALS_IDENTITY);
    }

    default public boolean contains(@NotNull Component that, @NotNull BiPredicate<? super Component, ? super Component> equals) {
        if (equals.test(this, that)) {
            return true;
        }
        for (Component child : this.children()) {
            if (!child.contains(that, equals)) continue;
            return true;
        }
        @Nullable HoverEvent<?> hoverEvent = this.hoverEvent();
        if (hoverEvent != null) {
            Object value = hoverEvent.value();
            Component component = null;
            if (value instanceof Component) {
                component = (Component)hoverEvent.value();
            } else if (value instanceof HoverEvent.ShowEntity) {
                component = ((HoverEvent.ShowEntity)value).name();
            }
            if (component != null) {
                if (equals.test(that, component)) {
                    return true;
                }
                for (Component child : component.children()) {
                    if (!child.contains(that, equals)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Deprecated
    default public void detectCycle(@NotNull Component that) {
        if (that.contains(this)) {
            throw new IllegalStateException("Component cycle detected between " + this + " and " + that);
        }
    }

    @Contract(pure=true)
    @NotNull
    public Component append(@NotNull Component var1);

    @NotNull
    default public Component append(@NotNull ComponentLike component) {
        return this.append(component.asComponent());
    }

    @Contract(pure=true)
    @NotNull
    default public Component append(@NotNull ComponentBuilder<?, ?> builder) {
        return this.append((Component)builder.build());
    }

    @NotNull
    public Style style();

    @Contract(pure=true)
    @NotNull
    public Component style(@NotNull Style var1);

    @Contract(pure=true)
    @NotNull
    default public Component style(@NotNull Consumer<Style.Builder> consumer) {
        return this.style(this.style().edit(consumer));
    }

    @Contract(pure=true)
    @NotNull
    default public Component style(@NotNull Consumer<Style.Builder> consumer, @NotNull Style.Merge.Strategy strategy) {
        return this.style(this.style().edit(consumer, strategy));
    }

    @Contract(pure=true)
    @NotNull
    default public Component style(@NotNull Style.Builder style) {
        return this.style(style.build());
    }

    @Contract(pure=true)
    @NotNull
    default public Component mergeStyle(@NotNull Component that) {
        return this.mergeStyle(that, Style.Merge.all());
    }

    @Contract(pure=true)
    @NotNull
    default public Component mergeStyle(@NotNull Component that, Style.Merge ... merges) {
        return this.mergeStyle(that, Style.Merge.of(merges));
    }

    @Contract(pure=true)
    @NotNull
    default public Component mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
        return this.style(this.style().merge(that.style(), merges));
    }

    @Nullable
    default public TextColor color() {
        return this.style().color();
    }

    @Contract(pure=true)
    @NotNull
    default public Component color(@Nullable TextColor color) {
        return this.style(this.style().color(color));
    }

    @Contract(pure=true)
    @NotNull
    default public Component colorIfAbsent(@Nullable TextColor color) {
        if (this.color() == null) {
            return this.color(color);
        }
        return this;
    }

    default public boolean hasDecoration(@NotNull TextDecoration decoration) {
        return this.decoration(decoration) == TextDecoration.State.TRUE;
    }

    @Contract(pure=true)
    @NotNull
    default public Component decorate(@NotNull TextDecoration decoration) {
        return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    default public @NotNull TextDecoration.State decoration(@NotNull TextDecoration decoration) {
        return this.style().decoration(decoration);
    }

    @Contract(pure=true)
    @NotNull
    default public Component decoration(@NotNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    @Contract(pure=true)
    @NotNull
    default public Component decoration(@NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
        return this.style(this.style().decoration(decoration, state));
    }

    @NotNull
    default public Map<TextDecoration, TextDecoration.State> decorations() {
        return this.style().decorations();
    }

    @Contract(pure=true)
    @NotNull
    default public Component decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
        return this.style(this.style().decorations(decorations));
    }

    @Nullable
    default public ClickEvent clickEvent() {
        return this.style().clickEvent();
    }

    @Contract(pure=true)
    @NotNull
    default public Component clickEvent(@Nullable ClickEvent event) {
        return this.style(this.style().clickEvent(event));
    }

    @Nullable
    default public HoverEvent<?> hoverEvent() {
        return this.style().hoverEvent();
    }

    @Contract(pure=true)
    @NotNull
    default public Component hoverEvent(@Nullable HoverEventSource<?> source) {
        return this.style(this.style().hoverEvent(source));
    }

    @Nullable
    default public String insertion() {
        return this.style().insertion();
    }

    @Contract(pure=true)
    @NotNull
    default public Component insertion(@Nullable String insertion) {
        return this.style(this.style().insertion(insertion));
    }

    default public boolean hasStyling() {
        return !this.style().isEmpty();
    }

    @Contract(pure=true)
    @NotNull
    public Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> var1);

    @Contract(pure=true)
    @NotNull
    public Component replaceText(@NotNull TextReplacementConfig var1);

    @NotNull
    public Component compact();

    @NotNull
    default public Iterable<Component> iterable(@NotNull ComponentIteratorType type, ComponentIteratorFlag ... flags) {
        return this.iterable(type, flags == null ? Collections.emptySet() : MonkeyBars.enumSet(ComponentIteratorFlag.class, (Enum[])flags));
    }

    @NotNull
    default public Iterable<Component> iterable(@NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(flags, "flags");
        return new ForwardingIterator<Component>(() -> this.iterator(type, flags), () -> this.spliterator(type, flags));
    }

    @NotNull
    default public Iterator<Component> iterator(@NotNull ComponentIteratorType type, ComponentIteratorFlag ... flags) {
        return this.iterator(type, flags == null ? Collections.emptySet() : MonkeyBars.enumSet(ComponentIteratorFlag.class, (Enum[])flags));
    }

    @NotNull
    default public Iterator<Component> iterator(@NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
        return new ComponentIterator(this, Objects.requireNonNull(type, "type"), Objects.requireNonNull(flags, "flags"));
    }

    @NotNull
    default public Spliterator<Component> spliterator(@NotNull ComponentIteratorType type, ComponentIteratorFlag ... flags) {
        return this.spliterator(type, flags == null ? Collections.emptySet() : MonkeyBars.enumSet(ComponentIteratorFlag.class, (Enum[])flags));
    }

    @NotNull
    default public Spliterator<Component> spliterator(@NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
        return Spliterators.spliteratorUnknownSize(this.iterator(type, flags), 0);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceText(@NotNull String search, @Nullable ComponentLike replacement) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.matchLiteral(search).replacement(replacement));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, @Nullable ComponentLike> replacement) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.match(pattern).replacement(replacement));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceFirstText(@NotNull String search, @Nullable ComponentLike replacement) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.matchLiteral(search).once().replacement(replacement));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceFirstText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, @Nullable ComponentLike> replacement) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.match(pattern).once().replacement(replacement));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceText(@NotNull String search, @Nullable ComponentLike replacement, int numberOfReplacements) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.matchLiteral(search).times(numberOfReplacements).replacement(replacement));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, @Nullable ComponentLike> replacement, int numberOfReplacements) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.match(pattern).times(numberOfReplacements).replacement(replacement));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceText(@NotNull String search, @Nullable ComponentLike replacement, @NotNull IntFunction2<PatternReplacementResult> fn) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.matchLiteral(search).replacement(replacement).condition(fn));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(pure=true)
    @NotNull
    default public Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, @Nullable ComponentLike> replacement, @NotNull IntFunction2<PatternReplacementResult> fn) {
        return this.replaceText((TextReplacementConfig.Builder b) -> b.match(pattern).replacement(replacement).condition(fn));
    }

    @Override
    default public void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
        component.append(this);
    }

    @Override
    @NotNull
    default public Component asComponent() {
        return this;
    }

    @Override
    @NotNull
    default public HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
        return HoverEvent.showText((Component)op.apply(this));
    }
}

