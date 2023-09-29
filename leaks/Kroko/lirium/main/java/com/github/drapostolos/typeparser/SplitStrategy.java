package com.github.drapostolos.typeparser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Callback interface that allows clients to define their own split strategy. The
 * {@link SplitStrategy} is used when splitting the input string to either {@link Collection},
 * {@link Map} or Array types.
 * <p>
 * Implementations of this interface are expected to be immutable.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
@FunctionalInterface
public interface SplitStrategy {

    /**
     * Splits {@code input} string and returns a list of substrings.
     * 
     * @param input pre-processed input string to parse. Will never be {@code null} or a
     *        {@code NullString} (See {@link NullStringStrategy}).
     * @param helper Helper class injected automatically by the {@link TypeParser}.
     * @return A list of strings computed by splitting the {@code input} string using this
     *         {@link SplitStrategy}.
     * @see ParserHelper#split(String)
     * @see ParserHelper#splitKeyValue(String)
     * @see InputPreprocessor
     * @see NullStringStrategy
     */
    List<String> split(String input, SplitStrategyHelper helper);

}
