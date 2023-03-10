/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class DecoderException
extends CodecException {
    private static final long serialVersionUID = 6926716840699621852L;

    public DecoderException() {
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(Throwable cause) {
        super(cause);
    }
}

