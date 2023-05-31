package com.github.drapostolos.typeparser;

import static com.github.drapostolos.typeparser.DynamicParsers.NoneContainerType.PROPERTY_EDITOR;
import static com.github.drapostolos.typeparser.Util.defaultSplitStrategy;
import static com.github.drapostolos.typeparser.Util.makeNullArgumentErrorMsg;
import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builder class for constructing and configuring instances of {@link TypeParser}.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
public final class TypeParserBuilder {

    final Map<Type, Parser<?>> parsers = Parsers.copyDefault();
    final Set<DynamicParser> defaultDynamicParsers = DynamicParsers.copyDefault();
    final List<DynamicParser> clientProvidedDynamicParsers = new ArrayList<DynamicParser>();
    SplitStrategy splitStrategy = defaultSplitStrategy();
    SplitStrategy keyValueSplitStrategy = (input, helper) -> asList(input.split("=", 2));
    InputPreprocessor inputPreprocessor = (input, helper) -> input;
    NullStringStrategy nullStringStrategy = (input, helper) -> "null".equalsIgnoreCase(input.trim());

    TypeParserBuilder() {
    }

    /**
     * Unregister the {@link Parser} associated with the given {@code targetType}.
     * {@code targetType} will be ignored if not associated with any {@link Parser}.
     * 
     * @param targetType The type associated with {@link Parser} to unregister.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if given argument is null.
     */
    public TypeParserBuilder unregisterParser(Class<?> targetType) {
        if (targetType == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("targetType"));
        }
        parsers.remove(targetType);
        return this;
    }

    /**
     * Register a custom made {@link Parser} implementation, associated with
     * the given {@code targetType}.
     * 
     * @param <T> the type associated with given {@code parser}.
     * @param targetType associated with given {@code parser}.
     * @param parser custom made {@link Parser} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     */
    public <T> TypeParserBuilder registerParser(Class<T> targetType, Parser<T> parser) {
        if (parser == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("parser"));
        }
        if (targetType == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("targetType"));
        }
        if (targetType.isArray()) {
            String message = "Cannot register Parser for array class. Register a Parser for "
                    + "the component type '%s' instead, as arrays are handled automatically "
                    + "internally in type-parser.";
            Class<?> componentType = targetType.getComponentType();
            throw new IllegalArgumentException(String.format(message, componentType.getName()));
        }
        parsers.put(targetType, parser);
        return this;
    }

    /**
     * Register a custom made {@link Parser} implementation, associated with
     * the given generic {@code targetType}.
     * 
     * @param <T> the generic type associated with given {@code parser}.
     * @param targetType generic type associated with given {@code parser}.
     * @param parser custom made {@link Parser} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     */
    public <T> TypeParserBuilder registerParser(GenericType<T> targetType, Parser<T> parser) {
        if (parser == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("parser"));
        }
        if (targetType == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("targetType"));
        }
        parsers.put(targetType.getType(), parser);
        return this;
    }

    /**
     * Register a custom made {@link DynamicParser} implementation. The {@link TypeParser} will loop
     * through the registered {@link DynamicParser} and the first found (which does not return
     * {@link DynamicParser#TRY_NEXT}) will be used. The first registered {@link DynamicParser} will
     * be first in the loop.
     * 
     * @param parser custom made {@link DynamicParser} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     */
    public TypeParserBuilder registerDynamicParser(DynamicParser parser) {
        if (parser == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("parser"));
        }
        clientProvidedDynamicParsers.add(parser);
        return this;
    }

    /**
     * Set a custom made {@link SplitStrategy} implementation to be used by
     * the {@link TypeParser}.
     * <p>
     * The default behavior, when parsing a string to either a {@link Collection}, {@link Map} or
     * Array type is to split the <code>input</code> string by comma (',').
     * <p>
     * Use this method to register your own {@link SplitStrategy} implementation to override the
     * default behavior.
     * 
     * @param splitStrategy {@link SplitStrategy} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     */
    public TypeParserBuilder setSplitStrategy(SplitStrategy splitStrategy) {
        if (splitStrategy == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("splitStrategy"));
        }
        this.splitStrategy = splitStrategy;
        return this;
    }

    /**
     * Set a custom made {@link SplitStrategy} implementation to separate the {@code key} and
     * {@code value} pair in a Map entry.
     * <p>
     * The default behavior, when parsing a string to a {@link Map} instance, is to split each map
     * entry by a "=" sign. For example this input string: <code>"key1=valueA,key2=valueB"</code><br>
     * will first be split using the {@link SplitStrategy} strategy as set with
     * {@link #setSplitStrategy(SplitStrategy)} to get a list of map entries (key/value pairs.
     * Example: ["key1=valueA", "key2=valueB"]). Then each map entry is split by "=" where first
     * element is the {@code key} and the second element is the {@code value}.
     * <p>
     * Use this method to register your own key/value {@link SplitStrategy} implementation to
     * override the default behavior. I.e replace using the "=" sign with some other character.
     * 
     * @param splitStrategy {@link SplitStrategy} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     */
    public TypeParserBuilder setKeyValueSplitStrategy(SplitStrategy splitStrategy) {
        if (splitStrategy == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("splitStrategy"));
        }
        this.keyValueSplitStrategy = splitStrategy;
        return this;
    }

    /**
     * Set a custom made {@link InputPreprocessor} implementation to be used by
     * the {@link TypeParser}.
     * <p>
     * By default, the pre-processing is doing nothing. Use this method to set your own
     * {@link InputPreprocessor} implementation to override the default behavior.
     * 
     * @param inputPreprocessor {@link InputPreprocessor} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     */
    public TypeParserBuilder setInputPreprocessor(InputPreprocessor inputPreprocessor) {
        if (inputPreprocessor == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("inputPreprocessor"));
        }
        this.inputPreprocessor = inputPreprocessor;
        return this;
    }

    /**
     * Set a custom made {@link NullStringStrategy} implementation to be used by the
     * {@link TypeParser}.
     * <p>
     * The {@link NullStringStrategy} defines the string that will cause the {@link TypeParser} to
     * return either an empty type (applicable for {@link Collection}, {@link Map} and Array types)
     * or a null object. This String is known as the <code>NullString</code>.
     * </p>
     * By default the <code>NullString</code> is set to the (trimmed and case insensitive) string
     * "null". Examples follow: <br>
     * <code>
     * TypeParser parser = TypeParser.newBuilder().build();<br>
     * parser.parse("null", Integer.class); // returns a null object<br>
     * parser.parse("NULL", new GenericType&lt;List&lt;Integer&gt;&gt;() {}); // returns an empty List<br>
     * parser.parse(" null ", Integer[].class); // returns an empty Integer array<br>
     * </code>
     * Use this method to set your own {@link InputPreprocessor} implementation to override the
     * default behavior.
     * 
     * @param nullStringStrategy {@link NullStringStrategy} implementation.
     * @return {@link TypeParserBuilder}
     * @throws NullPointerException if any given argument is null.
     * @see NullStringStrategy
     */
    public TypeParserBuilder setNullStringStrategy(NullStringStrategy nullStringStrategy) {
        if (nullStringStrategy == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("nullStringStrategy"));
        }
        this.nullStringStrategy = nullStringStrategy;
        return this;
    }

    public TypeParserBuilder enablePropertyEditor() {
        defaultDynamicParsers.add(PROPERTY_EDITOR);
        return this;
    }

    /**
     * Constructs a new instance of {@link TypeParser} as configured
     * with this {@link TypeParserBuilder}.
     * 
     * @return new instance of {@link TypeParser}.
     */
    public TypeParser build() {
        return new TypeParser(this);
    }
}
