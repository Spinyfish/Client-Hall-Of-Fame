package com.github.drapostolos.typeparser;

import java.util.Collection;
import java.util.Map;

/**
 * Callback interface that allows clients define their own {@code NullString}. Implementations of
 * this interface are expected to be immutable.
 * <p>
 * A {@code NullString} is string which will cause the {@link TypeParser} to return either an empty
 * type (applicable for {@link Collection}, {@link Map} and Array types) or a null object.
 * <p>
 * By default the <code>NullString</code> is set to the (trimmed and case insensitive) string
 * "null". Examples follow: <br>
 * <code>
 * TypeParser parser = TypeParser.newBuilder().build();<br>
 * parser.parse("null", Integer.class); // returns a null object<br>
 * parser.parse("NULL", new GenericType&lt;List&lt;Integer&gt;&gt;() {}); // returns an empty List<br>
 * parser.parse(" null ", Integer[].class); // returns an empty Integer array<br>
 * </code>
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 * @see TypeParserBuilder#setNullStringStrategy(NullStringStrategy)
 */
@FunctionalInterface
public interface NullStringStrategy {

    /**
     * Checks if the given <code>input</code> is a <code>NullString</code> or not.
     * 
     * @param input pre-processed input string to parse. Will never be {@code null}.
     * @param helper Helper class injected automatically by the {@link TypeParser}.
     * @return true if the given input String is the {@code NullString} otherwise false.
     * @throws RuntimeException Any exception thrown within this method will be wrapped and
     *         re-thrown as a {@link TypeParserException} to the client.
     * @see InputPreprocessor
     */
    boolean isNullString(String input, NullStringStrategyHelper helper);

}
