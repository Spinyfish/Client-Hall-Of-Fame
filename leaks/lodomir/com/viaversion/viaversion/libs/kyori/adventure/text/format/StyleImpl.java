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
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.AlwaysMerger;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.IfAbsentOnTargetMerger;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Merger;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class StyleImpl
implements Style {
    static final StyleImpl EMPTY = new StyleImpl(null, null, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, null, null, null);
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();
    @Nullable
    final Key font;
    @Nullable
    final TextColor color;
    final TextDecoration.State obfuscated;
    final TextDecoration.State bold;
    final TextDecoration.State strikethrough;
    final TextDecoration.State underlined;
    final TextDecoration.State italic;
    @Nullable
    final ClickEvent clickEvent;
    @Nullable
    final HoverEvent<?> hoverEvent;
    @Nullable
    final String insertion;

    static void decorate(Style.Builder builder, TextDecoration[] decorations) {
        for (TextDecoration decoration : decorations) {
            builder.decoration(decoration, true);
        }
    }

    StyleImpl(@Nullable Key font, @Nullable TextColor color, TextDecoration.State obfuscated, TextDecoration.State bold, TextDecoration.State strikethrough, TextDecoration.State underlined, TextDecoration.State italic, @Nullable ClickEvent clickEvent, @Nullable HoverEvent<?> hoverEvent, @Nullable String insertion) {
        this.font = font;
        this.color = color;
        this.obfuscated = obfuscated;
        this.bold = bold;
        this.strikethrough = strikethrough;
        this.underlined = underlined;
        this.italic = italic;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.insertion = insertion;
    }

    @Override
    @Nullable
    public Key font() {
        return this.font;
    }

    @Override
    @NotNull
    public Style font(@Nullable Key font) {
        if (Objects.equals(this.font, font)) {
            return this;
        }
        return new StyleImpl(font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    }

    @Override
    @Nullable
    public TextColor color() {
        return this.color;
    }

    @Override
    @NotNull
    public Style color(@Nullable TextColor color) {
        if (Objects.equals(this.color, color)) {
            return this;
        }
        return new StyleImpl(this.font, color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    }

    @Override
    @NotNull
    public Style colorIfAbsent(@Nullable TextColor color) {
        if (this.color == null) {
            return this.color(color);
        }
        return this;
    }

    @Override
    public @NotNull TextDecoration.State decoration(@NotNull TextDecoration decoration) {
        if (decoration == TextDecoration.BOLD) {
            return this.bold;
        }
        if (decoration == TextDecoration.ITALIC) {
            return this.italic;
        }
        if (decoration == TextDecoration.UNDERLINED) {
            return this.underlined;
        }
        if (decoration == TextDecoration.STRIKETHROUGH) {
            return this.strikethrough;
        }
        if (decoration == TextDecoration.OBFUSCATED) {
            return this.obfuscated;
        }
        throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
    }

    @Override
    @NotNull
    public Style decoration(@NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
        Objects.requireNonNull(state, "state");
        if (decoration == TextDecoration.BOLD) {
            return new StyleImpl(this.font, this.color, this.obfuscated, state, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.ITALIC) {
            return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, state, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.UNDERLINED) {
            return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, state, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.STRIKETHROUGH) {
            return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, state, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.OBFUSCATED) {
            return new StyleImpl(this.font, this.color, state, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
    }

    @Override
    @NotNull
    public Map<TextDecoration, TextDecoration.State> decorations() {
        EnumMap<TextDecoration, TextDecoration.State> decorations = new EnumMap<TextDecoration, TextDecoration.State>(TextDecoration.class);
        for (TextDecoration decoration : DECORATIONS) {
            TextDecoration.State value = this.decoration(decoration);
            decorations.put(decoration, value);
        }
        return decorations;
    }

    @Override
    @NotNull
    public Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
        TextDecoration.State obfuscated = decorations.getOrDefault(TextDecoration.OBFUSCATED, this.obfuscated);
        TextDecoration.State bold = decorations.getOrDefault(TextDecoration.BOLD, this.bold);
        TextDecoration.State strikethrough = decorations.getOrDefault(TextDecoration.STRIKETHROUGH, this.strikethrough);
        TextDecoration.State underlined = decorations.getOrDefault(TextDecoration.UNDERLINED, this.underlined);
        TextDecoration.State italic = decorations.getOrDefault(TextDecoration.ITALIC, this.italic);
        return new StyleImpl(this.font, this.color, obfuscated, bold, strikethrough, underlined, italic, this.clickEvent, this.hoverEvent, this.insertion);
    }

    @Override
    @Nullable
    public ClickEvent clickEvent() {
        return this.clickEvent;
    }

    @Override
    @NotNull
    public Style clickEvent(@Nullable ClickEvent event) {
        return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, event, this.hoverEvent, this.insertion);
    }

    @Override
    @Nullable
    public HoverEvent<?> hoverEvent() {
        return this.hoverEvent;
    }

    @Override
    @NotNull
    public Style hoverEvent(@Nullable HoverEventSource<?> source) {
        return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, HoverEventSource.unbox(source), this.insertion);
    }

    @Override
    @Nullable
    public String insertion() {
        return this.insertion;
    }

    @Override
    @NotNull
    public Style insertion(@Nullable String insertion) {
        if (Objects.equals(this.insertion, insertion)) {
            return this;
        }
        return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, insertion);
    }

    @Override
    @NotNull
    public Style merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
        if (that.isEmpty() || strategy == Style.Merge.Strategy.NEVER || merges.isEmpty()) {
            return this;
        }
        if (this.isEmpty() && Style.Merge.hasAll(merges)) {
            return that;
        }
        Style.Builder builder = this.toBuilder();
        builder.merge(that, strategy, merges);
        return builder.build();
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    @NotNull
    public Style.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("color", this.color), ExaminableProperty.of("obfuscated", (Object)this.obfuscated), ExaminableProperty.of("bold", (Object)this.bold), ExaminableProperty.of("strikethrough", (Object)this.strikethrough), ExaminableProperty.of("underlined", (Object)this.underlined), ExaminableProperty.of("italic", (Object)this.italic), ExaminableProperty.of("clickEvent", this.clickEvent), ExaminableProperty.of("hoverEvent", this.hoverEvent), ExaminableProperty.of("insertion", this.insertion), ExaminableProperty.of("font", this.font));
    }

    @NotNull
    public String toString() {
        return this.examine(StringExaminer.simpleEscaping());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StyleImpl)) {
            return false;
        }
        StyleImpl that = (StyleImpl)other;
        return Objects.equals(this.color, that.color) && this.obfuscated == that.obfuscated && this.bold == that.bold && this.strikethrough == that.strikethrough && this.underlined == that.underlined && this.italic == that.italic && Objects.equals(this.clickEvent, that.clickEvent) && Objects.equals(this.hoverEvent, that.hoverEvent) && Objects.equals(this.insertion, that.insertion) && Objects.equals(this.font, that.font);
    }

    public int hashCode() {
        int result = Objects.hashCode(this.color);
        result = 31 * result + this.obfuscated.hashCode();
        result = 31 * result + this.bold.hashCode();
        result = 31 * result + this.strikethrough.hashCode();
        result = 31 * result + this.underlined.hashCode();
        result = 31 * result + this.italic.hashCode();
        result = 31 * result + Objects.hashCode(this.clickEvent);
        result = 31 * result + Objects.hashCode(this.hoverEvent);
        result = 31 * result + Objects.hashCode(this.insertion);
        result = 31 * result + Objects.hashCode(this.font);
        return result;
    }

    static final class BuilderImpl
    implements Style.Builder {
        @Nullable
        Key font;
        @Nullable
        TextColor color;
        TextDecoration.State obfuscated = TextDecoration.State.NOT_SET;
        TextDecoration.State bold = TextDecoration.State.NOT_SET;
        TextDecoration.State strikethrough = TextDecoration.State.NOT_SET;
        TextDecoration.State underlined = TextDecoration.State.NOT_SET;
        TextDecoration.State italic = TextDecoration.State.NOT_SET;
        @Nullable
        ClickEvent clickEvent;
        @Nullable
        HoverEvent<?> hoverEvent;
        @Nullable
        String insertion;

        BuilderImpl() {
        }

        BuilderImpl(@NotNull StyleImpl style) {
            this.color = style.color;
            this.obfuscated = style.obfuscated;
            this.bold = style.bold;
            this.strikethrough = style.strikethrough;
            this.underlined = style.underlined;
            this.italic = style.italic;
            this.clickEvent = style.clickEvent;
            this.hoverEvent = style.hoverEvent;
            this.insertion = style.insertion;
            this.font = style.font;
        }

        @Override
        @NotNull
        public Style.Builder font(@Nullable Key font) {
            this.font = font;
            return this;
        }

        @Override
        @NotNull
        public Style.Builder color(@Nullable TextColor color) {
            this.color = color;
            return this;
        }

        @Override
        @NotNull
        public Style.Builder colorIfAbsent(@Nullable TextColor color) {
            if (this.color == null) {
                this.color = color;
            }
            return this;
        }

        @Override
        @NotNull
        public Style.Builder decorate(@NotNull TextDecoration decoration) {
            return this.decoration(decoration, TextDecoration.State.TRUE);
        }

        @Override
        @NotNull
        public Style.Builder decorate(TextDecoration ... decorations) {
            int length = decorations.length;
            for (int i = 0; i < length; ++i) {
                this.decorate(decorations[i]);
            }
            return this;
        }

        @Override
        @NotNull
        public Style.Builder decoration(@NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
            Objects.requireNonNull(state, "state");
            if (decoration == TextDecoration.BOLD) {
                this.bold = state;
                return this;
            }
            if (decoration == TextDecoration.ITALIC) {
                this.italic = state;
                return this;
            }
            if (decoration == TextDecoration.UNDERLINED) {
                this.underlined = state;
                return this;
            }
            if (decoration == TextDecoration.STRIKETHROUGH) {
                this.strikethrough = state;
                return this;
            }
            if (decoration == TextDecoration.OBFUSCATED) {
                this.obfuscated = state;
                return this;
            }
            throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
        }

        @NotNull
        Style.Builder decorationIfAbsent(@NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
            Objects.requireNonNull(state, "state");
            if (decoration == TextDecoration.BOLD) {
                if (this.bold == TextDecoration.State.NOT_SET) {
                    this.bold = state;
                }
                return this;
            }
            if (decoration == TextDecoration.ITALIC) {
                if (this.italic == TextDecoration.State.NOT_SET) {
                    this.italic = state;
                }
                return this;
            }
            if (decoration == TextDecoration.UNDERLINED) {
                if (this.underlined == TextDecoration.State.NOT_SET) {
                    this.underlined = state;
                }
                return this;
            }
            if (decoration == TextDecoration.STRIKETHROUGH) {
                if (this.strikethrough == TextDecoration.State.NOT_SET) {
                    this.strikethrough = state;
                }
                return this;
            }
            if (decoration == TextDecoration.OBFUSCATED) {
                if (this.obfuscated == TextDecoration.State.NOT_SET) {
                    this.obfuscated = state;
                }
                return this;
            }
            throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
        }

        @Override
        @NotNull
        public Style.Builder clickEvent(@Nullable ClickEvent event) {
            this.clickEvent = event;
            return this;
        }

        @Override
        @NotNull
        public Style.Builder hoverEvent(@Nullable HoverEventSource<?> source) {
            this.hoverEvent = HoverEventSource.unbox(source);
            return this;
        }

        @Override
        @NotNull
        public Style.Builder insertion(@Nullable String insertion) {
            this.insertion = insertion;
            return this;
        }

        @Override
        @NotNull
        public Style.Builder merge(@NotNull Style that, @NotNull Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
            Key font;
            String insertion;
            TextColor color;
            if (strategy == Style.Merge.Strategy.NEVER || that.isEmpty() || merges.isEmpty()) {
                return this;
            }
            Merger merger = BuilderImpl.merger(strategy);
            if (merges.contains((Object)Style.Merge.COLOR) && (color = that.color()) != null) {
                merger.mergeColor(this, color);
            }
            if (merges.contains((Object)Style.Merge.DECORATIONS)) {
                int length = DECORATIONS.length;
                for (int i = 0; i < length; ++i) {
                    TextDecoration decoration = DECORATIONS[i];
                    TextDecoration.State state = that.decoration(decoration);
                    if (state == TextDecoration.State.NOT_SET) continue;
                    merger.mergeDecoration(this, decoration, state);
                }
            }
            if (merges.contains((Object)Style.Merge.EVENTS)) {
                HoverEvent<?> hoverEvent;
                ClickEvent clickEvent = that.clickEvent();
                if (clickEvent != null) {
                    merger.mergeClickEvent(this, clickEvent);
                }
                if ((hoverEvent = that.hoverEvent()) != null) {
                    merger.mergeHoverEvent(this, hoverEvent);
                }
            }
            if (merges.contains((Object)Style.Merge.INSERTION) && (insertion = that.insertion()) != null) {
                merger.mergeInsertion(this, insertion);
            }
            if (merges.contains((Object)Style.Merge.FONT) && (font = that.font()) != null) {
                merger.mergeFont(this, font);
            }
            return this;
        }

        private static Merger merger(Style.Merge.Strategy strategy) {
            if (strategy == Style.Merge.Strategy.ALWAYS) {
                return AlwaysMerger.INSTANCE;
            }
            if (strategy == Style.Merge.Strategy.NEVER) {
                throw new UnsupportedOperationException();
            }
            if (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET) {
                return IfAbsentOnTargetMerger.INSTANCE;
            }
            throw new IllegalArgumentException(strategy.name());
        }

        @Override
        @NotNull
        public StyleImpl build() {
            if (this.isEmpty()) {
                return EMPTY;
            }
            return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }

        private boolean isEmpty() {
            return this.color == null && this.obfuscated == TextDecoration.State.NOT_SET && this.bold == TextDecoration.State.NOT_SET && this.strikethrough == TextDecoration.State.NOT_SET && this.underlined == TextDecoration.State.NOT_SET && this.italic == TextDecoration.State.NOT_SET && this.clickEvent == null && this.hoverEvent == null && this.insertion == null && this.font == null;
        }
    }
}

