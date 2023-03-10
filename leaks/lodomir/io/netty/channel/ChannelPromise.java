/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public interface ChannelPromise
extends ChannelFuture,
Promise<Void> {
    @Override
    public Channel channel();

    public ChannelPromise setSuccess(Void var1);

    public ChannelPromise setSuccess();

    public boolean trySuccess();

    public ChannelPromise setFailure(Throwable var1);

    @Override
    public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelPromise sync() throws InterruptedException;

    @Override
    public ChannelPromise syncUninterruptibly();

    @Override
    public ChannelPromise await() throws InterruptedException;

    @Override
    public ChannelPromise awaitUninterruptibly();
}

