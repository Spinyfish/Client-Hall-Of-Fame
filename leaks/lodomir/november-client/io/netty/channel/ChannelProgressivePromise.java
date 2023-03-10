/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressivePromise;

public interface ChannelProgressivePromise
extends ProgressivePromise<Void>,
ChannelProgressiveFuture,
ChannelPromise {
    @Override
    public ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelProgressivePromise sync() throws InterruptedException;

    @Override
    public ChannelProgressivePromise syncUninterruptibly();

    @Override
    public ChannelProgressivePromise await() throws InterruptedException;

    @Override
    public ChannelProgressivePromise awaitUninterruptibly();

    @Override
    public ChannelProgressivePromise setSuccess(Void var1);

    @Override
    public ChannelProgressivePromise setSuccess();

    @Override
    public ChannelProgressivePromise setFailure(Throwable var1);

    public ChannelProgressivePromise setProgress(long var1, long var3);
}

