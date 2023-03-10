/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelException;

public class ChannelPipelineException
extends ChannelException {
    private static final long serialVersionUID = 3379174210419885980L;

    public ChannelPipelineException() {
    }

    public ChannelPipelineException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelPipelineException(String message) {
        super(message);
    }

    public ChannelPipelineException(Throwable cause) {
        super(cause);
    }
}

