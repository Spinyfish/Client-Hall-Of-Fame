/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class UnsupportedMessageTypeException
extends CodecException {
    private static final long serialVersionUID = 2799598826487038726L;

    public UnsupportedMessageTypeException(Object message, Class<?> ... expectedTypes) {
        super(UnsupportedMessageTypeException.message(message == null ? "null" : message.getClass().getName(), expectedTypes));
    }

    public UnsupportedMessageTypeException() {
    }

    public UnsupportedMessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedMessageTypeException(String s) {
        super(s);
    }

    public UnsupportedMessageTypeException(Throwable cause) {
        super(cause);
    }

    private static String message(String actualType, Class<?> ... expectedTypes) {
        StringBuilder buf = new StringBuilder(actualType);
        if (expectedTypes != null && expectedTypes.length > 0) {
            Class<?> t;
            buf.append(" (expected: ").append(expectedTypes[0].getName());
            for (int i = 1; i < expectedTypes.length && (t = expectedTypes[i]) != null; ++i) {
                buf.append(", ").append(t.getName());
            }
            buf.append(')');
        }
        return buf.toString();
    }
}

