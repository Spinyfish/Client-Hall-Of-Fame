/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import java.net.ConnectException;
import java.net.SocketAddress;

public abstract class AbstractOioChannel
extends AbstractChannel {
    protected static final int SO_TIMEOUT = 1000;
    private volatile boolean readPending;
    private final Runnable readTask = new Runnable(){

        @Override
        public void run() {
            if (!AbstractOioChannel.this.isReadPending() && !AbstractOioChannel.this.config().isAutoRead()) {
                return;
            }
            AbstractOioChannel.this.setReadPending(false);
            AbstractOioChannel.this.doRead();
        }
    };

    protected AbstractOioChannel(Channel parent) {
        super(parent);
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new DefaultOioUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof ThreadPerChannelEventLoop;
    }

    protected abstract void doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

    @Override
    protected void doBeginRead() throws Exception {
        if (this.isReadPending()) {
            return;
        }
        this.setReadPending(true);
        this.eventLoop().execute(this.readTask);
    }

    protected abstract void doRead();

    protected boolean isReadPending() {
        return this.readPending;
    }

    protected void setReadPending(boolean readPending) {
        this.readPending = readPending;
    }

    private final class DefaultOioUnsafe
    extends AbstractChannel.AbstractUnsafe {
        private DefaultOioUnsafe() {
        }

        @Override
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                boolean wasActive = AbstractOioChannel.this.isActive();
                AbstractOioChannel.this.doConnect(remoteAddress, localAddress);
                this.safeSetSuccess(promise);
                if (!wasActive && AbstractOioChannel.this.isActive()) {
                    AbstractOioChannel.this.pipeline().fireChannelActive();
                }
            }
            catch (Throwable t2) {
                ConnectException t2;
                if (t2 instanceof ConnectException) {
                    ConnectException newT = new ConnectException(t2.getMessage() + ": " + remoteAddress);
                    newT.setStackTrace(t2.getStackTrace());
                    t2 = newT;
                }
                this.safeSetFailure(promise, t2);
                this.closeIfClosed();
            }
        }
    }
}

