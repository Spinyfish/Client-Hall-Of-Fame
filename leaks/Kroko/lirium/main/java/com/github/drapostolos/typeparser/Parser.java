package com.github.drapostolos.typeparser;

/**
 * Callback interface that allows clients to parse a given string and then convert it to
 * a specific type. Implementations of this interface are expected to be immutable.
 * 
 * @param <T> the type to convert the parsed string to.
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
@FunctionalInterface
public interface Parser<T> {

    /**
     * Parses the given string and converts it to an instance of type T.
     * <p>
     * If the input string passed to the {@link TypeParser} is the {@code NullString} (See
     * {@link NullStringStrategy}), then this method will be skipped and a {@code null} object is
     * automatically return from {@link TypeParser}.
     * <p>
     * 
     * @param input pre-processed input string to parse. Will never be {@code null} or a
     *        {@code NullString}.
     * @param helper Helper class injected automatically by the {@link TypeParser}.
     * @return an instance of type T.
     * @throws RuntimeException Any exception thrown within this method will be wrapped and
     *         re-thrown as a {@link TypeParserException} to the client.
     * @see NullStringStrategy
     */
    T parse(String input, ParserHelper helper);
}
