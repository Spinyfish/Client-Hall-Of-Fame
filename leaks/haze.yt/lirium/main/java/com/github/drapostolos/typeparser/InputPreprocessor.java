package com.github.drapostolos.typeparser;

/**
 * Callback interface that allows clients to do their own preparation of the input string to parse.
 * Implementations of this interface are expected to be immutable.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
@FunctionalInterface
public interface InputPreprocessor {

    /**
     * Prepares the input string to be parsed. Must return a {@link String} object.
     * <p>
     * Returning a <code>null</code> object is not supported. If a null object is return, an
     * {@link UnsupportedOperationException} will be thrown.
     * 
     * @param input String to prepare for parsing. This may be a {@code NullString} but will never
     *        be {@code null}.
     * @param helper Helper class injected automatically by the {@link TypeParser}.
     * @return a prepared string to be parsed.
     * @throws RuntimeException Any exception thrown within this method will be wrapped and
     *         re-thrown as a {@link TypeParserException} to the client.
     */
    String prepare(String input, InputPreprocessorHelper helper);
}
