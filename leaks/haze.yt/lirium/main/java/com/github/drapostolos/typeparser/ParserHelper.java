package com.github.drapostolos.typeparser;

import static com.github.drapostolos.typeparser.Util.makeNullArgumentErrorMsg;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Helper class providing helper methods to implementations of {@link Parser} when parsing a
 * string to a type.
 * <p>
 * The {@link TypeParser} will automatically inject an instance of this class into the
 * {@link Parser} implementation.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
public final class ParserHelper extends Helper {

    private final TargetType targetType;
    private final TypeParser typeParser;
    private final SplitStrategy splitStrategy;
    private final SplitStrategy keyValueSplitStrategy;
    private final NullStringStrategy nullStringStrategy;

    ParserHelper(TargetType targetType, TypeParser typeParser) {
        super(targetType);
        this.targetType = targetType;
        this.typeParser = typeParser;
        this.splitStrategy = typeParser.splitStrategy;
        this.nullStringStrategy = typeParser.nullStringStrategy;
        this.keyValueSplitStrategy = typeParser.keyValueSplitStrategy;
    }

    /**
     * This method gives access to {@link TypeParser#parse(String, Class)}.
     * 
     * @param <T> the expected type to convert {@code input} to.
     * @param input String to parse.
     * @param targetType to parse it to.
     * @return an instance of type.
     */
    public <T> T parse(String input, Class<T> targetType) {
        return typeParser.parse(input, targetType);
    }

    /**
     * This method gives access to {@link TypeParser#parseType(String, Type)}.
     * 
     * @param input String to parse.
     * @param targetType The target type to parse the given input to.
     * @return an instance of type.
     */
    public Object parseType(String input, Type targetType) {
        return typeParser.parseType(input, targetType);
    }

    /**
     * Splits the {@code input} string into a list of sub-strings by using the {@link SplitStrategy}
     * implementation (as registered with {@link TypeParserBuilder#setSplitStrategy(SplitStrategy)}
     * ).
     * <p>
     * If {@code input} is the <code>NullString</code> (See {@link NullStringStrategy}), an empty
     * list is returned, without calling the registered {@link SplitStrategy}.
     * <p>
     * For example the default {@link SplitStrategy} will split this string "1, 2, 3, 4" into ["1",
     * " 2", " 3", " 4"].
     * <p>
     * 
     * @param input pre-processed input string to parse. Can be a {@code NullString}.
     * @return List of strings, or an empty List if the given <code>input</code> is the
     *         <code>NullString</code> (See {@link NullStringStrategy})
     * @throws NullPointerException if given {@code input} is null.
     * @throws RuntimeException for any other faults that happens within the {@link SplitStrategy}.
     * @see NullStringStrategy
     * @see SplitStrategy
     * @see InputPreprocessor
     */
    public List<String> split(String input) {
        if (input == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("input"));
        }
        if (nullStringStrategy.isNullString(input, new NullStringStrategyHelper(targetType))) {
            return Collections.emptyList();
        }
        return splitStrategy.split(input, new SplitStrategyHelper(targetType));
    }

    /**
     * Splits the {@code keyValue} string into a list of two string elements by using the
     * {@link SplitStrategy} implementation (as registered with
     * {@link TypeParserBuilder#setKeyValueSplitStrategy(SplitStrategy)}). The first element
     * represent the <code>Key</code> and the second element the <code>Value</code>.
     * <p>
     * For example the default key-value {@link SplitStrategy} splits this string "a=AAA=BBB" into
     * ["a", "AAA=BBB"]. Note! The the string is only split by the first occurring of "=", any
     * subsequent "=" are ignored by the default key-value {@link SplitStrategy}.
     * 
     * @param keyValue Example: "Key=Value"
     * @return A list of two string elements computed by splitting the {@code keyValue} string using
     *         the key-value SplitStrategy.
     * @throws NullPointerException if given {@code keyValue} is null.
     * @throws RuntimeException for any other faults that happens within the {@link SplitStrategy}.
     * @see SplitStrategy
     */
    public List<String> splitKeyValue(String keyValue) {
        if (keyValue == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("keyValue"));
        }
        return keyValueSplitStrategy.split(keyValue, new SplitStrategyHelper(targetType));
    }

    /**
     * Checks if the given <code>input</code> is considered a <code>NullString</code> or not.
     * Returns true if it is, otherwise returns false.
     * 
     * @param input string to parse.
     * @return true if <code>input</code> is a <code>NullString</code>, otherwise false.
     * @throws NullPointerException if given argument is {@code null}.
     * @see NullStringStrategy
     */
    public boolean isNullString(String input) {
        if (input == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("input"));
        }
        return nullStringStrategy.isNullString(input, new NullStringStrategyHelper(targetType));
    }
}
