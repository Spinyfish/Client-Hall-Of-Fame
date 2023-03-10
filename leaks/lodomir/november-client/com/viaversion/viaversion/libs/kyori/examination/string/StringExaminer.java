/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.examination.string;

import com.viaversion.viaversion.libs.kyori.examination.AbstractExaminer;
import com.viaversion.viaversion.libs.kyori.examination.string.Strings;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringExaminer
extends AbstractExaminer<String> {
    private static final Function<String, String> DEFAULT_ESCAPER = string -> string.replace("\"", "\\\"").replace("\\", "\\\\").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    private static final Collector<CharSequence, ?, String> COMMA_CURLY = Collectors.joining(", ", "{", "}");
    private static final Collector<CharSequence, ?, String> COMMA_SQUARE = Collectors.joining(", ", "[", "]");
    private final Function<String, String> escaper;

    @NotNull
    public static StringExaminer simpleEscaping() {
        return Instances.SIMPLE_ESCAPING;
    }

    public StringExaminer(@NotNull Function<String, String> escaper) {
        this.escaper = escaper;
    }

    @Override
    @NotNull
    protected <E> String array(E @NotNull [] array, @NotNull Stream<String> elements) {
        return elements.collect(COMMA_SQUARE);
    }

    @Override
    @NotNull
    protected <E> String collection(@NotNull Collection<E> collection, @NotNull Stream<String> elements) {
        return elements.collect(COMMA_SQUARE);
    }

    @Override
    @NotNull
    protected String examinable(@NotNull String name, @NotNull Stream<Map.Entry<String, String>> properties) {
        return name + properties.map(property -> (String)property.getKey() + '=' + (String)property.getValue()).collect(COMMA_CURLY);
    }

    @Override
    @NotNull
    protected <K, V> String map(@NotNull Map<K, V> map, @NotNull Stream<Map.Entry<String, String>> entries) {
        return entries.map(entry -> (String)entry.getKey() + '=' + (String)entry.getValue()).collect(COMMA_CURLY);
    }

    @Override
    @NotNull
    protected String nil() {
        return "null";
    }

    @Override
    @NotNull
    protected String scalar(@NotNull Object value) {
        return String.valueOf(value);
    }

    @Override
    @NotNull
    public String examine(boolean value) {
        return String.valueOf(value);
    }

    @Override
    @NotNull
    public String examine(byte value) {
        return String.valueOf(value);
    }

    @Override
    @NotNull
    public String examine(char value) {
        return Strings.wrapIn(this.escaper.apply(String.valueOf(value)), '\'');
    }

    @Override
    @NotNull
    public String examine(double value) {
        return Strings.withSuffix(String.valueOf(value), 'd');
    }

    @Override
    @NotNull
    public String examine(float value) {
        return Strings.withSuffix(String.valueOf(value), 'f');
    }

    @Override
    @NotNull
    public String examine(int value) {
        return String.valueOf(value);
    }

    @Override
    @NotNull
    public String examine(long value) {
        return String.valueOf(value);
    }

    @Override
    @NotNull
    public String examine(short value) {
        return String.valueOf(value);
    }

    @Override
    @NotNull
    protected <T> String stream(@NotNull Stream<T> stream) {
        return stream.map(this::examine).collect(COMMA_SQUARE);
    }

    @Override
    @NotNull
    protected String stream(@NotNull DoubleStream stream) {
        return stream.mapToObj(this::examine).collect(COMMA_SQUARE);
    }

    @Override
    @NotNull
    protected String stream(@NotNull IntStream stream) {
        return stream.mapToObj(this::examine).collect(COMMA_SQUARE);
    }

    @Override
    @NotNull
    protected String stream(@NotNull LongStream stream) {
        return stream.mapToObj(this::examine).collect(COMMA_SQUARE);
    }

    @Override
    @NotNull
    public String examine(@Nullable String value) {
        if (value == null) {
            return this.nil();
        }
        return Strings.wrapIn(this.escaper.apply(value), '\"');
    }

    @Override
    @NotNull
    protected String array(int length, IntFunction<String> value) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < length; ++i) {
            sb.append(value.apply(i));
            if (i + 1 >= length) continue;
            sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }

    static /* synthetic */ Function access$000() {
        return DEFAULT_ESCAPER;
    }

    private static final class Instances {
        static final StringExaminer SIMPLE_ESCAPING = new StringExaminer(StringExaminer.access$000());

        private Instances() {
        }
    }
}

