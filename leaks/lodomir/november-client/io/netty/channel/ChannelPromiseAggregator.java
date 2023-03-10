/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ChannelPromiseAggregator
implements ChannelFutureListener {
    private final ChannelPromise aggregatePromise;
    private Set<ChannelPromise> pendingPromises;

    public ChannelPromiseAggregator(ChannelPromise aggregatePromise) {
        if (aggregatePromise == null) {
            throw new NullPointerException("aggregatePromise");
        }
        this.aggregatePromise = aggregatePromise;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChannelPromiseAggregator add(ChannelPromise ... promises) {
        if (promises == null) {
            throw new NullPointerException("promises");
        }
        if (promises.length == 0) {
            return this;
        }
        ChannelPromiseAggregator channelPromiseAggregator = this;
        synchronized (channelPromiseAggregator) {
            if (this.pendingPromises == null) {
                int size = promises.length > 1 ? promises.length : 2;
                this.pendingPromises = new LinkedHashSet<ChannelPromise>(size);
            }
            for (ChannelPromise p : promises) {
                if (p == null) continue;
                this.pendingPromises.add(p);
                p.addListener(this);
            }
        }
        return this;
    }

    @Override
    public synchronized void operationComplete(ChannelFuture future) throws Exception {
        if (this.pendingPromises == null) {
            this.aggregatePromise.setSuccess();
        } else {
            this.pendingPromises.remove(future);
            if (!future.isSuccess()) {
                this.aggregatePromise.setFailure(future.cause());
                for (ChannelPromise pendingFuture : this.pendingPromises) {
                    pendingFuture.setFailure(future.cause());
                }
            } else if (this.pendingPromises.isEmpty()) {
                this.aggregatePromise.setSuccess();
            }
        }
    }
}

