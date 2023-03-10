/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DecoderException;

public class TooLongFrameException
extends DecoderException {
    private static final long serialVersionUID = -1995801950698951640L;

    public TooLongFrameException() {
    }

    public TooLongFrameException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooLongFrameException(String message) {
        super(message);
    }

    public TooLongFrameException(Throwable cause) {
        super(cause);
    }
}

