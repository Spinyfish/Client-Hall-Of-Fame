/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextReplacementConfig;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.flattener.ComponentFlattener;
import com.viaversion.viaversion.libs.kyori.adventure.text.flattener.FlattenerListener;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.NamedTextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextFormat;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyFormat;
import com.viaversion.viaversion.libs.kyori.adventure.util.Services;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class LegacyComponentSerializerImpl
implements LegacyComponentSerializer {
    static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();
    private static final char LEGACY_BUNGEE_HEX_CHAR = 'x';
    private static final List<TextFormat> FORMATS;
    private static final String LEGACY_CHARS;
    private static final Optional<LegacyComponentSerializer.Provider> SERVICE;
    static final Consumer<LegacyComponentSerializer.Builder> BUILDER;
    private final char character;
    private final char hexCharacter;
    @Nullable
    private final TextReplacementConfig urlReplacementConfig;
    private final boolean hexColours;
    private final boolean useTerriblyStupidHexFormat;
    private final ComponentFlattener flattener;

    LegacyComponentSerializerImpl(char character, char hexCharacter, @Nullable TextReplacementConfig urlReplacementConfig, boolean hexColours, boolean useTerriblyStupidHexFormat, ComponentFlattener flattener) {
        this.character = character;
        this.hexCharacter = hexCharacter;
        this.urlReplacementConfig = urlReplacementConfig;
        this.hexColours = hexColours;
        this.useTerriblyStupidHexFormat = useTerriblyStupidHexFormat;
        this.flattener = flattener;
    }

    @Nullable
    private FormatCodeType determineFormatType(char legacy, String input, int pos) {
        if (pos >= 14) {
            int expectedCharacterPosition = pos - 14;
            int expectedIndicatorPosition = pos - 13;
            if (input.charAt(expectedCharacterPosition) == this.character && input.charAt(expectedIndicatorPosition) == 'x') {
                return FormatCodeType.BUNGEECORD_UNUSUAL_HEX;
            }
        }
        if (legacy == this.hexCharacter && input.length() - pos >= 6) {
            return FormatCodeType.KYORI_HEX;
        }
        if (LEGACY_CHARS.indexOf(legacy) != -1) {
            return FormatCodeType.MOJANG_LEGACY;
        }
        return null;
    }

    @Nullable
    static LegacyFormat legacyFormat(char character) {
        int index = LEGACY_CHARS.indexOf(character);
        if (index != -1) {
            TextFormat format = FORMATS.get(index);
            if (format instanceof NamedTextColor) {
                return new LegacyFormat((NamedTextColor)format);
            }
            if (format instanceof TextDecoration) {
                return new LegacyFormat((TextDecoration)format);
            }
            if (format instanceof Reset) {
                return LegacyFormat.RESET;
            }
        }
        return null;
    }

    @Nullable
    private DecodedFormat decodeTextFormat(char legacy, String input, int pos) {
        FormatCodeType foundFormat = this.determineFormatType(legacy, input, pos);
        if (foundFormat == null) {
            return null;
        }
        if (foundFormat == FormatCodeType.KYORI_HEX) {
            @Nullable TextColor parsed = LegacyComponentSerializerImpl.tryParseHexColor(input.substring(pos, pos + 6));
            if (parsed != null) {
                return new DecodedFormat(foundFormat, parsed);
            }
        } else {
            if (foundFormat == FormatCodeType.MOJANG_LEGACY) {
                return new DecodedFormat(foundFormat, FORMATS.get(LEGACY_CHARS.indexOf(legacy)));
            }
            if (foundFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX) {
                StringBuilder foundHex = new StringBuilder(6);
                for (int i = pos - 1; i >= pos - 11; i -= 2) {
                    foundHex.append(input.charAt(i));
                }
                @Nullable TextColor parsed = LegacyComponentSerializerImpl.tryParseHexColor(foundHex.reverse().toString());
                if (parsed != null) {
                    return new DecodedFormat(foundFormat, parsed);
                }
            }
        }
        return null;
    }

    @Nullable
    private static TextColor tryParseHexColor(String hexDigits) {
        try {
            int color = Integer.parseInt(hexDigits, 16);
            return TextColor.color(color);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

    private static boolean isHexTextColor(TextFormat format) {
        return format instanceof TextColor && !(format instanceof NamedTextColor);
    }

    private String toLegacyCode(TextFormat format) {
        if (LegacyComponentSerializerImpl.isHexTextColor(format)) {
            TextColor color = (TextColor)format;
            if (this.hexColours) {
                String hex = String.format("%06x", color.value());
                if (this.useTerriblyStupidHexFormat) {
                    StringBuilder legacy = new StringBuilder(String.valueOf('x'));
                    for (char character : hex.toCharArray()) {
                        legacy.append(this.character).append(character);
                    }
                    return legacy.toString();
                }
                return this.hexCharacter + hex;
            }
            format = NamedTextColor.nearestTo(color);
        }
        int index = FORMATS.indexOf(format);
        return Character.toString(LEGACY_CHARS.charAt(index));
    }

    private TextComponent extractUrl(TextComponent component) {
        if (this.urlReplacementConfig == null) {
            return component;
        }
        Component newComponent = component.replaceText(this.urlReplacementConfig);
        if (newComponent instanceof TextComponent) {
            return (TextComponent)newComponent;
        }
        return (TextComponent)((TextComponent.Builder)Component.text().append(newComponent)).build();
    }

    @Override
    @NotNull
    public TextComponent deserialize(@NotNull String input) {
        String remaining;
        int next = input.lastIndexOf(this.character, input.length() - 2);
        if (next == -1) {
            return this.extractUrl(Component.text(input));
        }
        ArrayList<TextComponent> parts = new ArrayList<TextComponent>();
        TextComponent.Builder current = null;
        boolean reset = false;
        int pos = input.length();
        do {
            DecodedFormat decoded;
            if ((decoded = this.decodeTextFormat(input.charAt(next + 1), input, next + 2)) == null) continue;
            int from = next + (decoded.encodedFormat == FormatCodeType.KYORI_HEX ? 8 : 2);
            if (from != pos) {
                if (current != null) {
                    if (reset) {
                        parts.add((TextComponent)current.build());
                        reset = false;
                        current = Component.text();
                    } else {
                        current = (TextComponent.Builder)Component.text().append((Component)current.build());
                    }
                } else {
                    current = Component.text();
                }
                current.content(input.substring(from, pos));
            } else if (current == null) {
                current = Component.text();
            }
            if (!reset) {
                reset = LegacyComponentSerializerImpl.applyFormat(current, decoded.format);
            }
            if (decoded.encodedFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX) {
                next -= 12;
            }
            pos = next;
        } while ((next = input.lastIndexOf(this.character, next - 1)) != -1);
        if (current != null) {
            parts.add((TextComponent)current.build());
        }
        String string = remaining = pos > 0 ? input.substring(0, pos) : "";
        if (parts.size() == 1 && remaining.isEmpty()) {
            return this.extractUrl((TextComponent)parts.get(0));
        }
        Collections.reverse(parts);
        return this.extractUrl((TextComponent)((TextComponent.Builder)Component.text().content(remaining).append(parts)).build());
    }

    @Override
    @NotNull
    public String serialize(@NotNull Component component) {
        Cereal state = new Cereal();
        this.flattener.flatten(component, state);
        return state.toString();
    }

    private static boolean applyFormat(@NotNull TextComponent.Builder builder, @NotNull TextFormat format) {
        if (format instanceof TextColor) {
            builder.colorIfAbsent((TextColor)format);
            return true;
        }
        if (format instanceof TextDecoration) {
            builder.decoration((TextDecoration)format, TextDecoration.State.TRUE);
            return false;
        }
        if (format instanceof Reset) {
            return true;
        }
        throw new IllegalArgumentException(String.format("unknown format '%s'", format.getClass()));
    }

    @Override
    @NotNull
    public LegacyComponentSerializer.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static /* synthetic */ Optional access$000() {
        return SERVICE;
    }

    static {
        LinkedHashMap<Object, String> formats = new LinkedHashMap<Object, String>(22);
        formats.put(NamedTextColor.BLACK, "0");
        formats.put(NamedTextColor.DARK_BLUE, "1");
        formats.put(NamedTextColor.DARK_GREEN, "2");
        formats.put(NamedTextColor.DARK_AQUA, "3");
        formats.put(NamedTextColor.DARK_RED, "4");
        formats.put(NamedTextColor.DARK_PURPLE, "5");
        formats.put(NamedTextColor.GOLD, "6");
        formats.put(NamedTextColor.GRAY, "7");
        formats.put(NamedTextColor.DARK_GRAY, "8");
        formats.put(NamedTextColor.BLUE, "9");
        formats.put(NamedTextColor.GREEN, "a");
        formats.put(NamedTextColor.AQUA, "b");
        formats.put(NamedTextColor.RED, "c");
        formats.put(NamedTextColor.LIGHT_PURPLE, "d");
        formats.put(NamedTextColor.YELLOW, "e");
        formats.put(NamedTextColor.WHITE, "f");
        formats.put(TextDecoration.OBFUSCATED, "k");
        formats.put(TextDecoration.BOLD, "l");
        formats.put(TextDecoration.STRIKETHROUGH, "m");
        formats.put(TextDecoration.UNDERLINED, "n");
        formats.put(TextDecoration.ITALIC, "o");
        formats.put(Reset.INSTANCE, "r");
        FORMATS = Collections.unmodifiableList(new ArrayList(formats.keySet()));
        LEGACY_CHARS = String.join((CharSequence)"", formats.values());
        if (FORMATS.size() != LEGACY_CHARS.length()) {
            throw new IllegalStateException("FORMATS length differs from LEGACY_CHARS length");
        }
        SERVICE = Services.service(LegacyComponentSerializer.Provider.class);
        BUILDER = SERVICE.map(LegacyComponentSerializer.Provider::legacy).orElseGet(() -> builder -> {});
    }

    static final class DecodedFormat {
        final FormatCodeType encodedFormat;
        final TextFormat format;

        private DecodedFormat(FormatCodeType encodedFormat, TextFormat format) {
            if (format == null) {
                throw new IllegalStateException("No format found");
            }
            this.encodedFormat = encodedFormat;
            this.format = format;
        }
    }

    static enum FormatCodeType {
        MOJANG_LEGACY,
        KYORI_HEX,
        BUNGEECORD_UNUSUAL_HEX;

    }

    static final class BuilderImpl
    implements LegacyComponentSerializer.Builder {
        private char character = (char)167;
        private char hexCharacter = (char)35;
        private TextReplacementConfig urlReplacementConfig = null;
        private boolean hexColours = false;
        private boolean useTerriblyStupidHexFormat = false;
        private ComponentFlattener flattener = ComponentFlattener.basic();

        BuilderImpl() {
            BUILDER.accept(this);
        }

        BuilderImpl(@NotNull LegacyComponentSerializerImpl serializer) {
            this();
            this.character = serializer.character;
            this.hexCharacter = serializer.hexCharacter;
            this.urlReplacementConfig = serializer.urlReplacementConfig;
            this.hexColours = serializer.hexColours;
            this.useTerriblyStupidHexFormat = serializer.useTerriblyStupidHexFormat;
            this.flattener = serializer.flattener;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder character(char legacyCharacter) {
            this.character = legacyCharacter;
            return this;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder hexCharacter(char legacyHexCharacter) {
            this.hexCharacter = legacyHexCharacter;
            return this;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder extractUrls() {
            return this.extractUrls(DEFAULT_URL_PATTERN, null);
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder extractUrls(@NotNull Pattern pattern) {
            return this.extractUrls(pattern, null);
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder extractUrls(@Nullable Style style) {
            return this.extractUrls(DEFAULT_URL_PATTERN, style);
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder extractUrls(@NotNull Pattern pattern, @Nullable Style style) {
            Objects.requireNonNull(pattern, "pattern");
            this.urlReplacementConfig = (TextReplacementConfig)TextReplacementConfig.builder().match(pattern).replacement(url -> {
                String clickUrl = url.content();
                if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                    clickUrl = "http://" + clickUrl;
                }
                return (style == null ? url : (TextComponent.Builder)url.style(style)).clickEvent(ClickEvent.openUrl(clickUrl));
            }).build();
            return this;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder hexColors() {
            this.hexColours = true;
            return this;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder useUnusualXRepeatedCharacterHexFormat() {
            this.useTerriblyStupidHexFormat = true;
            return this;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer.Builder flattener(@NotNull ComponentFlattener flattener) {
            this.flattener = Objects.requireNonNull(flattener, "flattener");
            return this;
        }

        @Override
        @NotNull
        public LegacyComponentSerializer build() {
            return new LegacyComponentSerializerImpl(this.character, this.hexCharacter, this.urlReplacementConfig, this.hexColours, this.useTerriblyStupidHexFormat, this.flattener);
        }
    }

    private final class Cereal
    implements FlattenerListener {
        private final StringBuilder sb = new StringBuilder();
        private final StyleState style = new StyleState();
        @Nullable
        private TextFormat lastWritten;
        private StyleState[] styles = new StyleState[8];
        private int head = -1;

        private Cereal() {
        }

        @Override
        public void pushStyle(@NotNull Style pushed) {
            StyleState state;
            int idx;
            if ((idx = ++this.head) >= this.styles.length) {
                this.styles = Arrays.copyOf(this.styles, this.styles.length * 2);
            }
            if ((state = this.styles[idx]) == null) {
                this.styles[idx] = state = new StyleState();
            }
            if (idx > 0) {
                state.set(this.styles[idx - 1]);
            } else {
                state.clear();
            }
            state.apply(pushed);
        }

        @Override
        public void component(@NotNull String text) {
            if (!text.isEmpty()) {
                if (this.head < 0) {
                    throw new IllegalStateException("No style has been pushed!");
                }
                this.styles[this.head].applyFormat();
                this.sb.append(text);
            }
        }

        @Override
        public void popStyle(@NotNull Style style) {
            if (this.head-- < 0) {
                throw new IllegalStateException("Tried to pop beyond what was pushed!");
            }
        }

        void append(@NotNull TextFormat format) {
            if (this.lastWritten != format) {
                this.sb.append(LegacyComponentSerializerImpl.this.character).append(LegacyComponentSerializerImpl.this.toLegacyCode(format));
            }
            this.lastWritten = format;
        }

        public String toString() {
            return this.sb.toString();
        }

        private final class StyleState {
            @Nullable
            private TextColor color;
            private final Set<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);
            private boolean needsReset;

            StyleState() {
            }

            void set(@NotNull StyleState that) {
                this.color = that.color;
                this.decorations.clear();
                this.decorations.addAll(that.decorations);
            }

            public void clear() {
                this.color = null;
                this.decorations.clear();
            }

            void apply(@NotNull Style component) {
                TextColor color = component.color();
                if (color != null) {
                    this.color = color;
                }
                int length = DECORATIONS.length;
                block4: for (int i = 0; i < length; ++i) {
                    TextDecoration decoration = DECORATIONS[i];
                    switch (component.decoration(decoration)) {
                        case TRUE: {
                            this.decorations.add(decoration);
                            continue block4;
                        }
                        case FALSE: {
                            if (!this.decorations.remove(decoration)) continue block4;
                            this.needsReset = true;
                        }
                    }
                }
            }

            void applyFormat() {
                boolean colorChanged;
                boolean bl = colorChanged = this.color != ((Cereal)Cereal.this).style.color;
                if (this.needsReset) {
                    if (!colorChanged) {
                        Cereal.this.append(Reset.INSTANCE);
                    }
                    this.needsReset = false;
                }
                if (colorChanged || Cereal.this.lastWritten == Reset.INSTANCE) {
                    this.applyFullFormat();
                    return;
                }
                if (!this.decorations.containsAll(((Cereal)Cereal.this).style.decorations)) {
                    this.applyFullFormat();
                    return;
                }
                for (TextDecoration decoration : this.decorations) {
                    if (!((Cereal)Cereal.this).style.decorations.add(decoration)) continue;
                    Cereal.this.append(decoration);
                }
            }

            private void applyFullFormat() {
                if (this.color != null) {
                    Cereal.this.append(this.color);
                } else {
                    Cereal.this.append(Reset.INSTANCE);
                }
                ((Cereal)Cereal.this).style.color = this.color;
                for (TextDecoration decoration : this.decorations) {
                    Cereal.this.append(decoration);
                }
                ((Cereal)Cereal.this).style.decorations.clear();
                ((Cereal)Cereal.this).style.decorations.addAll(this.decorations);
            }
        }
    }

    private static enum Reset implements TextFormat
    {
        INSTANCE;

    }

    static final class Instances {
        static final LegacyComponentSerializer SECTION = LegacyComponentSerializerImpl.access$000().map(LegacyComponentSerializer.Provider::legacySection).orElseGet(() -> new LegacyComponentSerializerImpl('\u00a7', '#', null, false, false, ComponentFlattener.basic()));
        static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializerImpl.access$000().map(LegacyComponentSerializer.Provider::legacyAmpersand).orElseGet(() -> new LegacyComponentSerializerImpl('&', '#', null, false, false, ComponentFlattener.basic()));

        Instances() {
        }
    }
}

