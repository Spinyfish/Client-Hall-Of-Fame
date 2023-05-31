package com.mojang.realmsclient.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public enum ChatFormatting {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    private static final Map<Character, ChatFormatting> FORMATTING_BY_CHAR = new HashMap();
    private static final Map<String, ChatFormatting> FORMATTING_BY_NAME = new HashMap();
    private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    private final char code;
    private final boolean isFormat;
    private final String toString;

    ChatFormatting(char code) {
        this(code, false);
    }

    ChatFormatting(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = "§" + code;
    }

    public char getChar() {
        return this.code;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    public static String stripFormatting(String input) {
        return input == null ? null : STRIP_FORMATTING_PATTERN.matcher(input).replaceAll("");
    }

    public static ChatFormatting getByChar(char code) {
        return (ChatFormatting)FORMATTING_BY_CHAR.get(code);
    }

    public static ChatFormatting getByName(String name) {
        return name == null ? null : (ChatFormatting)FORMATTING_BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    public static Collection<String> getNames(boolean getColors, boolean getFormats) {
        List<String> result = new ArrayList();
        ChatFormatting[] var3 = values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ChatFormatting format = var3[var5];
            if ((!format.isColor() || getColors) && (!format.isFormat() || getFormats)) {
                result.add(format.getName());
            }
        }

        return result;
    }

    static {
        ChatFormatting[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            ChatFormatting format = var0[var2];
            FORMATTING_BY_CHAR.put(format.getChar(), format);
            FORMATTING_BY_NAME.put(format.getName(), format);
        }

    }
}
