package com.github.drapostolos.typeparser;

/**
 * This wraps any unexpected {@link RuntimeException} thrown in parsing process
 * and propagates up to the client.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
public class TypeParserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    TypeParserException(String message, Throwable t) {
        super(message, t);
    }

}
