/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.examination.string;

import com.viaversion.viaversion.libs.kyori.examination.AbstractExaminer;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import com.viaversion.viaversion.libs.kyori.examination.string.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiLineStringExaminer
extends AbstractExaminer<Stream<String>> {
    private static final String INDENT_2 = "  ";
    private final StringExaminer examiner;

    @NotNull
    public static MultiLineStringExaminer simpleEscaping() {
        return Instances.SIMPLE_ESCAPING;
    }

    public MultiLineStringExaminer(@NotNull StringExaminer examiner) {
        this.examiner = examiner;
    }

    @Override
    @NotNull
    protected <E> Stream<String> array(E @NotNull [] array, @NotNull Stream<Stream<String>> elements) {
        return this.arrayLike(elements);
    }

    @Override
    @NotNull
    protected <E> Stream<String> collection(@NotNull Collection<E> collection, @NotNull Stream<Stream<String>> elements) {
        return this.arrayLike(elements);
    }

    @Override
    @NotNull
    protected Stream<String> examinable(@NotNull String name, @NotNull Stream<Map.Entry<String, Stream<String>>> properties) {
        Stream<String> flattened = MultiLineStringExaminer.flatten(",", properties.map(entry -> MultiLineStringExaminer.association((Stream<String>)this.examine((String)entry.getKey()), " = ", (Stream)entry.getValue())));
        Stream<String> indented = MultiLineStringExaminer.indent(flattened);
        return MultiLineStringExaminer.enclose(indented, name + "{", "}");
    }

    @Override
    @NotNull
    protected <K, V> Stream<String> map(@NotNull Map<K, V> map, @NotNull Stream<Map.Entry<Stream<String>, Stream<String>>> entries) {
        Stream<String> flattened = MultiLineStringExaminer.flatten(",", entries.map(entry -> MultiLineStringExaminer.association((Stream)entry.getKey(), " = ", (Stream)entry.getValue())));
        Stream<String> indented = MultiLineStringExaminer.indent(flattened);
        return MultiLineStringExaminer.enclose(indented, "{", "}");
    }

    @Override
    @NotNull
    protected Stream<String> nil() {
        return Stream.of(this.examiner.nil());
    }

    @Override
    @NotNull
    protected Stream<String> scalar(@NotNull Object value) {
        return Stream.of(this.examiner.scalar(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(boolean value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(byte value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(char value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(double value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(float value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(int value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(long value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    public Stream<String> examine(short value) {
        return Stream.of(this.examiner.examine(value));
    }

    @Override
    @NotNull
    protected Stream<String> array(int length, IntFunction<Stream<String>> value) {
        return this.arrayLike(length == 0 ? Stream.empty() : IntStream.range(0, length).mapToObj(value));
    }

    @Override
    @NotNull
    protected <T> Stream<String> stream(@NotNull Stream<T> stream) {
        return this.arrayLike(stream.map(this::examine));
    }

    @Override
    @NotNull
    protected Stream<String> stream(@NotNull DoubleStream stream) {
        return this.arrayLike(stream.mapToObj(d -> this.examine(d)));
    }

    @Override
    @NotNull
    protected Stream<String> stream(@NotNull IntStream stream) {
        return this.arrayLike(stream.mapToObj(n -> this.examine(n)));
    }

    @Override
    @NotNull
    protected Stream<String> stream(@NotNull LongStream stream) {
        return this.arrayLike(stream.mapToObj(l -> this.examine(l)));
    }

    @Override
    @NotNull
    public Stream<String> examine(@Nullable String value) {
        return Stream.of(this.examiner.examine(value));
    }

    private Stream<String> arrayLike(Stream<Stream<String>> streams) {
        Stream<String> flattened = MultiLineStringExaminer.flatten(",", streams);
        Stream<String> indented = MultiLineStringExaminer.indent(flattened);
        return MultiLineStringExaminer.enclose(indented, "[", "]");
    }

    private static Stream<String> enclose(Stream<String> lines, String open, String close) {
        return MultiLineStringExaminer.enclose(lines.collect(Collectors.toList()), open, close);
    }

    private static Stream<String> enclose(List<String> lines, String open, String close) {
        if (lines.isEmpty()) {
            return Stream.of(open + close);
        }
        return Stream.of(Stream.of(open), MultiLineStringExaminer.indent(lines.stream()), Stream.of(close)).reduce(Stream.empty(), Stream::concat);
    }

    private static Stream<String> flatten(String delimiter, Stream<Stream<String>> bumpy) {
        ArrayList flat = new ArrayList();
        bumpy.forEachOrdered(lines -> {
            if (!flat.isEmpty()) {
                int last = flat.size() - 1;
                flat.set(last, (String)flat.get(last) + delimiter);
            }
            lines.forEachOrdered(flat::add);
        });
        return flat.stream();
    }

    private static Stream<String> association(Stream<String> left, String middle, Stream<String> right) {
        return MultiLineStringExaminer.association(left.collect(Collectors.toList()), middle, right.collect(Collectors.toList()));
    }

    private static Stream<String> association(List<String> left, String middle, List<String> right) {
        int lefts = left.size();
        int rights = right.size();
        int height = Math.max(lefts, rights);
        int leftWidth = Strings.maxLength(left.stream());
        String leftPad = lefts < 2 ? "" : Strings.repeat(" ", leftWidth);
        String middlePad = lefts < 2 ? "" : Strings.repeat(" ", middle.length());
        ArrayList<String> result = new ArrayList<String>(height);
        for (int i = 0; i < height; ++i) {
            String l = i < lefts ? Strings.padEnd(left.get(i), leftWidth, ' ') : leftPad;
            String m = i == 0 ? middle : middlePad;
            String r = i < rights ? right.get(i) : "";
            result.add(l + m + r);
        }
        return result.stream();
    }

    private static Stream<String> indent(Stream<String> lines) {
        return lines.map(line -> INDENT_2 + line);
    }

    private static final class Instances {
        static final MultiLineStringExaminer SIMPLE_ESCAPING = new MultiLineStringExaminer(StringExaminer.simpleEscaping());

        private Instances() {
        }
    }
}

