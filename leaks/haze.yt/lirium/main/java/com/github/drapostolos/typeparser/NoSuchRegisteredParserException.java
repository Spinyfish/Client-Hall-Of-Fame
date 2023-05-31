package com.github.drapostolos.typeparser;

/**
 * A {@link NoSuchRegisteredParserException} is thrown when there is NO registered {@link Parser}
 * for a given {@code targetType}.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
public final class NoSuchRegisteredParserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    NoSuchRegisteredParserException(String message) {
        super(message);
    }
}
