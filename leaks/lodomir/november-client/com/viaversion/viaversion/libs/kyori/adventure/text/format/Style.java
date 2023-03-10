/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.StyleBuilderApplicable;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.StyleImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.adventure.util.MonkeyBars;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.NonExtendable
public interface Style
extends Buildable<Style, Builder>,
Examinable {
    public static final Key DEFAULT_FONT = Key.key("default");

    @NotNull
    public static Style empty() {
        return StyleImpl.EMPTY;
    }

    @NotNull
    public static Builder style() {
        return new StyleImpl.BuilderImpl();
    }

    @NotNull
    public static Style style(@NotNull Consumer<Builder> consumer) {
        return (Style)Buildable.configureAndBuild(Style.style(), consumer);
    }

    @NotNull
    public static Style style(@Nullable TextColor color) {
        if (color == null) {
            return Style.empty();
        }
        return new StyleImpl(null, color, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, null, null, null);
    }

    @NotNull
    public static Style style(@NotNull TextDecoration decoration) {
        return Style.style().decoration(decoration, true).build();
    }

    @NotNull
    public static Style style(@Nullable TextColor color, TextDecoration ... decorations) {
        Builder builder = Style.style();
        builder.color(color);
        StyleImpl.decorate(builder, decorations);
        return builder.build();
    }

    @NotNull
    public static Style style(@Nullable TextColor color, Set<TextDecoration> decorations) {
        Builder builder = Style.style();
        builder.color(color);
        if (!decorations.isEmpty()) {
            for (TextDecoration decoration : decorations) {
                builder.decoration(decoration, true);
            }
        }
        return builder.build();
    }

    @NotNull
    public static Style style(StyleBuilderApplicable ... applicables) {
        if (applicables.length == 0) {
            return Style.empty();
        }
        Builder builder = Style.style();
        int length = applicables.length;
        for (int i = 0; i < length; ++i) {
            applicables[i].styleApply(builder);
        }
        return builder.build();
    }

    @NotNull
    public static Style style(@NotNull Iterable<? extends StyleBuilderApplicable> applicables) {
        Builder builder = Style.style();
        for (StyleBuilderApplicable styleBuilderApplicable : applicables) {
            styleBuilderApplicable.styleApply(builder);
        }
        return builder.build();
    }

    @NotNull
    default public Style edit(@NotNull Consumer<Builder> consumer) {
        return this.edit(consumer, Merge.Strategy.ALWAYS);
    }

    @NotNull
    default public Style edit(@NotNull Consumer<Builder> consumer, @NotNull Merge.Strategy strategy) {
        return Style.style((Builder style) -> {
            if (strategy == Merge.Strategy.ALWAYS) {
                style.merge(this, strategy);
            }
            consumer.accept((Builder)style);
            if (strategy == Merge.Strategy.IF_ABSENT_ON_TARGET) {
                style.merge(this, strategy);
            }
        });
    }

    @Nullable
    public Key font();

    @NotNull
    public Style font(@Nullable Key var1);

    @Nullable
    public TextColor color();

    @NotNull
    public Style color(@Nullable TextColor var1);

    @NotNull
    public Style colorIfAbsent(@Nullable TextColor var1);

    default public boolean hasDecoration(@NotNull TextDecoration decoration) {
        return this.decoration(decoration) == TextDecoration.State.TRUE;
    }

    public @NotNull TextDecoration.State decoration(@NotNull TextDecoration var1);

    @NotNull
    default public Style decorate(@NotNull TextDecoration decoration) {
        return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    @NotNull
    default public Style decoration(@NotNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    @NotNull
    public Style decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

    public @Unmodifiable @NotNull Map<TextDecoration, TextDecoration.State> decorations();

    @NotNull
    public Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> var1);

    @Nullable
    public ClickEvent clickEvent();

    @NotNull
    public Style clickEvent(@Nullable ClickEvent var1);

    @Nullable
    public HoverEvent<?> hoverEvent();

    @NotNull
    public Style hoverEvent(@Nullable HoverEventSource<?> var1);

    @Nullable
    public String insertion();

    @NotNull
    public Style insertion(@Nullable String var1);

    @NotNull
    default public Style merge(@NotNull Style that) {
        return this.merge(that, Merge.all());
    }

    @NotNull
    default public Style merge(@NotNull Style that, @NotNull Merge.Strategy strategy) {
        return this.merge(that, strategy, Merge.all());
    }

    @NotNull
    default public Style merge(@NotNull Style that, @NotNull Merge merge) {
        return this.merge(that, Collections.singleton(merge));
    }

    @NotNull
    default public Style merge(@NotNull Style that, @NotNull Merge.Strategy strategy, @NotNull Merge merge) {
        return this.merge(that, strategy, Collections.singleton(merge));
    }

    @NotNull
    default public Style merge(@NotNull Style that, Merge ... merges) {
        return this.merge(that, Merge.of(merges));
    }

    @NotNull
    default public Style merge(@NotNull Style that, @NotNull Merge.Strategy strategy, Merge ... merges) {
        return this.merge(that, strategy, Merge.of(merges));
    }

    @NotNull
    default public Style merge(@NotNull Style that, @NotNull Set<Merge> merges) {
        return this.merge(that, Merge.Strategy.ALWAYS, merges);
    }

    @NotNull
    public Style merge(@NotNull Style var1, @NotNull Merge.Strategy var2, @NotNull Set<Merge> var3);

    public boolean isEmpty();

    @Override
    @NotNull
    public Builder toBuilder();

    public static interface Builder
    extends Buildable.Builder<Style> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder font(@Nullable Key var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder color(@Nullable TextColor var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder colorIfAbsent(@Nullable TextColor var1);

        @Contract(value="_ -> this")
        @NotNull
        default public Builder decorate(@NotNull TextDecoration decoration) {
            return this.decoration(decoration, TextDecoration.State.TRUE);
        }

        @Contract(value="_ -> this")
        @NotNull
        default public Builder decorate(TextDecoration ... decorations) {
            int length = decorations.length;
            for (int i = 0; i < length; ++i) {
                this.decorate(decorations[i]);
            }
            return this;
        }

        @Contract(value="_, _ -> this")
        @NotNull
        default public Builder decoration(@NotNull TextDecoration decoration, boolean flag) {
            return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
        }

        @Contract(value="_, _ -> this")
        @NotNull
        public Builder decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

        @Contract(value="_ -> this")
        @NotNull
        public Builder clickEvent(@Nullable ClickEvent var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder hoverEvent(@Nullable HoverEventSource<?> var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder insertion(@Nullable String var1);

        @Contract(value="_ -> this")
        @NotNull
        default public Builder merge(@NotNull Style that) {
            return this.merge(that, Merge.all());
        }

        @Contract(value="_, _ -> this")
        @NotNull
        default public Builder merge(@NotNull Style that, @NotNull Merge.Strategy strategy) {
            return this.merge(that, strategy, Merge.all());
        }

        @Contract(value="_, _ -> this")
        @NotNull
        default public Builder merge(@NotNull Style that, Merge ... merges) {
            if (merges.length == 0) {
                return this;
            }
            return this.merge(that, Merge.of(merges));
        }

        @Contract(value="_, _, _ -> this")
        @NotNull
        default public Builder merge(@NotNull Style that, @NotNull Merge.Strategy strategy, Merge ... merges) {
            if (merges.length == 0) {
                return this;
            }
            return this.merge(that, strategy, Merge.of(merges));
        }

        @Contract(value="_, _ -> this")
        @NotNull
        default public Builder merge(@NotNull Style that, @NotNull Set<Merge> merges) {
            return this.merge(that, Merge.Strategy.ALWAYS, merges);
        }

        @Contract(value="_, _, _ -> this")
        @NotNull
        public Builder merge(@NotNull Style var1, @NotNull Merge.Strategy var2, @NotNull Set<Merge> var3);

        @Contract(value="_ -> this")
        @NotNull
        default public Builder apply(@NotNull StyleBuilderApplicable applicable) {
            applicable.styleApply(this);
            return this;
        }

        @Override
        @NotNull
        public Style build();
    }

    public static enum Merge {
        COLOR,
        DECORATIONS,
        EVENTS,
        INSERTION,
        FONT;

        static final Set<Merge> ALL;
        static final Set<Merge> COLOR_AND_DECORATIONS;

        public static @Unmodifiable @NotNull Set<Merge> all() {
            return ALL;
        }

        public static @Unmodifiable @NotNull Set<Merge> colorAndDecorations() {
            return COLOR_AND_DECORATIONS;
        }

        public static @Unmodifiable @NotNull Set<Merge> of(Merge ... merges) {
            return MonkeyBars.enumSet(Merge.class, (Enum[])merges);
        }

        static boolean hasAll(@NotNull Set<Merge> merges) {
            return merges.size() == ALL.size();
        }

        static {
            ALL = Merge.of(Merge.values());
            COLOR_AND_DECORATIONS = Merge.of(COLOR, DECORATIONS);
        }

        public static enum Strategy {
            ALWAYS,
            NEVER,
            IF_ABSENT_ON_TARGET;

        }
    }
}

