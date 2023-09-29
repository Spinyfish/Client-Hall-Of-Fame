package com.github.drapostolos.typeparser;

/**
 * Callback interface that allows clients to define a {@link Parser} that decides during
 * runtime if it can parse the {@code input} string for a given {@code targetType}.
 * <p>
 * If a DynamicParser can not parse, then the {@link DynamicParser#parse(String, ParserHelper)}
 * method is expected to return the {@link DynamicParser#TRY_NEXT} constant.
 * <p>
 * The {@link TypeParser} will internally call all registered {@link DynamicParser} until the first
 * one found that does NOT return a {@link DynamicParser#TRY_NEXT} object.
 * <p>
 * Implementations of this interface are expected to be immutable.
 * <p>
 * Register {@link DynamicParser}s with this method
 * {@link TypeParserBuilder#registerDynamicParser(DynamicParser)}.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
@FunctionalInterface
public interface DynamicParser extends Parser<Object> {

    /**
     * The next available {@link DynamicParser} will be called if this constant is returned from
     * the {@link #parse(String, ParserHelper)} method.
     */
    public static final Object TRY_NEXT = new Object();

    /**
     * Parses the given string and converts it to an instance of {@code targetType}.
     * If not possible to parse, then {@link DynamicParser#TRY_NEXT} constant must
     * be returned by implementation.
     * 
     * @param input pre-processed input string to parse. This may be a {@code NullString} and
     *        implementations of this interface are expected to return a sensible value in such
     *        case. Will never be {@code null}.
     * @param helper Helper class injected automatically by the {@link TypeParser}.
     * @return an instance of the {@code targetType}, or {@link DynamicParser#TRY_NEXT} if not
     *         possible to parse.
     * @throws RuntimeException Any exception thrown within this method will be wrapped and
     *         re-thrown as a {@link TypeParserException} to the client.
     * @see NullStringStrategy
     * @see InputPreprocessor
     */
    @Override
    Object parse(String input, ParserHelper helper);
}
