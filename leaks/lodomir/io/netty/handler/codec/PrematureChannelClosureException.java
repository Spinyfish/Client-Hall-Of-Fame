/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class PrematureChannelClosureException
extends CodecException {
    private static final long serialVersionUID = 4907642202594703094L;

    public PrematureChannelClosureException() {
    }

    public PrematureChannelClosureException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrematureChannelClosureException(String message) {
        super(message);
    }

    public PrematureChannelClosureException(Throwable cause) {
        super(cause);
    }
}

