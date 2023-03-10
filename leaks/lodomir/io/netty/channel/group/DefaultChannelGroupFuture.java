/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupException;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

final class DefaultChannelGroupFuture
extends DefaultPromise<Void>
implements ChannelGroupFuture {
    private final ChannelGroup group;
    private final Map<Channel, ChannelFuture> futures;
    private int successCount;
    private int failureCount;
    private final ChannelFutureListener childListener = new ChannelFutureListener(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            boolean callSetDone;
            boolean success = future.isSuccess();
            DefaultChannelGroupFuture defaultChannelGroupFuture = DefaultChannelGroupFuture.this;
            synchronized (defaultChannelGroupFuture) {
                if (success) {
                    DefaultChannelGroupFuture.this.successCount++;
                } else {
                    DefaultChannelGroupFuture.this.failureCount++;
                }
                boolean bl = callSetDone = DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size();
                assert (DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size());
            }
            if (callSetDone) {
                if (DefaultChannelGroupFuture.this.failureCount > 0) {
                    ArrayList<Map.Entry<Channel, Throwable>> failed = new ArrayList<Map.Entry<Channel, Throwable>>(DefaultChannelGroupFuture.this.failureCount);
                    for (ChannelFuture f : DefaultChannelGroupFuture.this.futures.values()) {
                        if (f.isSuccess()) continue;
                        failed.add(new DefaultEntry<Channel, Throwable>(f.channel(), f.cause()));
                    }
                    DefaultChannelGroupFuture.this.setFailure0(new ChannelGroupException(failed));
                } else {
                    DefaultChannelGroupFuture.this.setSuccess0();
                }
            }
        }
    };

    DefaultChannelGroupFuture(ChannelGroup group, Collection<ChannelFuture> futures, EventExecutor executor) {
        super(executor);
        if (group == null) {
            throw new NullPointerException("group");
        }
        if (futures == null) {
            throw new NullPointerException("futures");
        }
        this.group = group;
        LinkedHashMap<Channel, ChannelFuture> futureMap = new LinkedHashMap<Channel, ChannelFuture>();
        for (ChannelFuture f : futures) {
            futureMap.put(f.channel(), f);
        }
        this.futures = Collections.unmodifiableMap(futureMap);
        for (ChannelFuture f : this.futures.values()) {
            f.addListener(this.childListener);
        }
        if (this.futures.isEmpty()) {
            this.setSuccess0();
        }
    }

    DefaultChannelGroupFuture(ChannelGroup group, Map<Channel, ChannelFuture> futures, EventExecutor executor) {
        super(executor);
        this.group = group;
        this.futures = Collections.unmodifiableMap(futures);
        for (ChannelFuture f : this.futures.values()) {
            f.addListener(this.childListener);
        }
        if (this.futures.isEmpty()) {
            this.setSuccess0();
        }
    }

    @Override
    public ChannelGroup group() {
        return this.group;
    }

    @Override
    public ChannelFuture find(Channel channel) {
        return this.futures.get(channel);
    }

    @Override
    public Iterator<ChannelFuture> iterator() {
        return this.futures.values().iterator();
    }

    @Override
    public synchronized boolean isPartialSuccess() {
        return this.successCount != 0 && this.successCount != this.futures.size();
    }

    @Override
    public synchronized boolean isPartialFailure() {
        return this.failureCount != 0 && this.failureCount != this.futures.size();
    }

    @Override
    public DefaultChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        super.addListeners(listeners);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        super.removeListeners(listeners);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelGroupException cause() {
        return (ChannelGroupException)super.cause();
    }

    private void setSuccess0() {
        super.setSuccess(null);
    }

    private void setFailure0(ChannelGroupException cause) {
        super.setFailure(cause);
    }

    public DefaultChannelGroupFuture setSuccess(Void result) {
        throw new IllegalStateException();
    }

    @Override
    public boolean trySuccess(Void result) {
        throw new IllegalStateException();
    }

    public DefaultChannelGroupFuture setFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    @Override
    protected void checkDeadLock() {
        EventExecutor e = this.executor();
        if (e != null && e != ImmediateEventExecutor.INSTANCE && e.inEventLoop()) {
            throw new BlockingOperationException();
        }
    }

    private static final class DefaultEntry<K, V>
    implements Map.Entry<K, V> {
        private final K key;
        private final V value;

        DefaultEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("read-only");
        }
    }
}

