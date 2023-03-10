/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollServerSocketChannelConfig;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.ServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class EpollServerSocketChannel
extends AbstractEpollChannel
implements ServerSocketChannel {
    private final EpollServerSocketChannelConfig config = new EpollServerSocketChannelConfig(this);
    private volatile InetSocketAddress local;

    public EpollServerSocketChannel() {
        super(Native.socketStreamFd(), 4);
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        InetSocketAddress addr = (InetSocketAddress)localAddress;
        EpollServerSocketChannel.checkResolvable(addr);
        Native.bind(this.fd, addr.getAddress(), addr.getPort());
        this.local = Native.localAddress(this.fd);
        Native.listen(this.fd, this.config.getBacklog());
        this.active = true;
    }

    @Override
    public EpollServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected InetSocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected InetSocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollServerSocketUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    final class EpollServerSocketUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        EpollServerSocketUnsafe() {
        }

        @Override
        public void connect(SocketAddress socketAddress, SocketAddress socketAddress2, ChannelPromise channelPromise) {
            channelPromise.setFailure(new UnsupportedOperationException());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        void epollInReady() {
            assert (EpollServerSocketChannel.this.eventLoop().inEventLoop());
            ChannelPipeline pipeline = EpollServerSocketChannel.this.pipeline();
            Throwable exception = null;
            try {
                try {
                    int socketFd;
                    while ((socketFd = Native.accept(EpollServerSocketChannel.this.fd)) != -1) {
                        try {
                            this.readPending = false;
                            pipeline.fireChannelRead(new EpollSocketChannel(EpollServerSocketChannel.this, socketFd));
                        }
                        catch (Throwable t) {
                            pipeline.fireChannelReadComplete();
                            pipeline.fireExceptionCaught(t);
                        }
                    }
                }
                catch (Throwable t) {
                    exception = t;
                }
                pipeline.fireChannelReadComplete();
                if (exception == null) return;
                pipeline.fireExceptionCaught(exception);
                return;
            }
            finally {
                if (!EpollServerSocketChannel.this.config.isAutoRead() && !this.readPending) {
                    this.clearEpollIn0();
                }
            }
        }
    }
}

