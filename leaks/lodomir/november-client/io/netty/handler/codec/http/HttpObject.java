/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public interface HttpObject {
    public DecoderResult getDecoderResult();

    public void setDecoderResult(DecoderResult var1);
}

