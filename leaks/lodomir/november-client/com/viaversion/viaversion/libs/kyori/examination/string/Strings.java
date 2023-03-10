/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.examination.string;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

final class Strings {
    private Strings() {
    }

    @NotNull
    static String withSuffix(String string, char suffix) {
        return string + suffix;
    }

    @NotNull
    static String wrapIn(String string, char wrap) {
        return wrap + string + wrap;
    }

    static int maxLength(Stream<String> strings) {
        return strings.mapToInt(String::length).max().orElse(0);
    }

    @NotNull
    static String repeat(@NotNull String string, int count) {
        if (count == 0) {
            return "";
        }
        if (count == 1) {
            return string;
        }
        StringBuilder sb = new StringBuilder(string.length() * count);
        for (int i = 0; i < count; ++i) {
            sb.append(string);
        }
        return sb.toString();
    }

    @NotNull
    static String padEnd(@NotNull String string, int minLength, char padding) {
        return string.length() >= minLength ? string : String.format("%-" + minLength + "s", Character.valueOf(padding));
    }
}

