package net.optifine.expr;

public class ParseException extends Exception {
    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParseException(final String message) {
        super(message);
    }
}
