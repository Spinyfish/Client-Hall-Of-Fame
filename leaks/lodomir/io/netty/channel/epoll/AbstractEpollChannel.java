/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.Native;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.OneTimeTask;
import java.net.InetSocketAddress;
import java.nio.channels.UnresolvedAddressException;

abstract class AbstractEpollChannel
extends AbstractChannel {
    private static final ChannelMetadata DATA = new ChannelMetadata(false);
    private final int readFlag;
    protected int flags;
    protected volatile boolean active;
    volatile int fd;
    int id;

    AbstractEpollChannel(int fd, int flag) {
        this(null, fd, flag, false);
    }

    AbstractEpollChannel(Channel parent, int fd, int flag, boolean active) {
        super(parent);
        this.fd = fd;
        this.readFlag = flag;
        this.flags |= flag;
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public ChannelMetadata metadata() {
        return DATA;
    }

    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.doDeregister();
        int fd = this.fd;
        this.fd = -1;
        Native.close(fd);
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    public boolean isOpen() {
        return this.fd != -1;
    }

    @Override
    protected void doDeregister() throws Exception {
        ((EpollEventLoop)this.eventLoop()).remove(this);
    }

    @Override
    protected void doBeginRead() throws Exception {
        ((AbstractEpollUnsafe)this.unsafe()).readPending = true;
        if ((this.flags & this.readFlag) == 0) {
            this.flags |= this.readFlag;
            this.modifyEvents();
        }
    }

    final void clearEpollIn() {
        if (this.isRegistered()) {
            EventLoop loop = this.eventLoop();
            final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
            if (loop.inEventLoop()) {
                unsafe.clearEpollIn0();
            } else {
                loop.execute(new OneTimeTask(){

                    @Override
                    public void run() {
                        if (!AbstractEpollChannel.this.config().isAutoRead() && !unsafe.readPending) {
                            unsafe.clearEpollIn0();
                        }
                    }
                });
            }
        } else {
            this.flags &= ~this.readFlag;
        }
    }

    protected final void setEpollOut() {
        if ((this.flags & 2) == 0) {
            this.flags |= 2;
            this.modifyEvents();
        }
    }

    protected final void clearEpollOut() {
        if ((this.flags & 2) != 0) {
            this.flags &= 0xFFFFFFFD;
            this.modifyEvents();
        }
    }

    private void modifyEvents() {
        if (this.isOpen()) {
            ((EpollEventLoop)this.eventLoop()).modify(this);
        }
    }

    @Override
    protected void doRegister() throws Exception {
        EpollEventLoop loop = (EpollEventLoop)this.eventLoop();
        loop.add(this);
    }

    @Override
    protected abstract AbstractEpollUnsafe newUnsafe();

    protected final ByteBuf newDirectBuffer(ByteBuf buf) {
        return this.newDirectBuffer(buf, buf);
    }

    protected final ByteBuf newDirectBuffer(Object holder, ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.safeRelease(holder);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            return AbstractEpollChannel.newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) {
            return AbstractEpollChannel.newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }

    private static ByteBuf newDirectBuffer0(Object holder, ByteBuf buf, ByteBufAllocator alloc, int capacity) {
        ByteBuf directBuf = alloc.directBuffer(capacity);
        directBuf.writeBytes(buf, buf.readerIndex(), capacity);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }

    protected static void checkResolvable(InetSocketAddress addr) {
        if (addr.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }

    protected abstract class AbstractEpollUnsafe
    extends AbstractChannel.AbstractUnsafe {
        protected boolean readPending;

        protected AbstractEpollUnsafe() {
        }

        abstract void epollInReady();

        void epollRdHupReady() {
        }

        @Override
        protected void flush0() {
            if (this.isFlushPending()) {
                return;
            }
            super.flush0();
        }

        void epollOutReady() {
            super.flush0();
        }

        private boolean isFlushPending() {
            return (AbstractEpollChannel.this.flags & 2) != 0;
        }

        protected final void clearEpollIn0() {
            if ((AbstractEpollChannel.this.flags & AbstractEpollChannel.this.readFlag) != 0) {
                AbstractEpollChannel.this.flags &= ~AbstractEpollChannel.this.readFlag;
                AbstractEpollChannel.this.modifyEvents();
            }
        }
    }
}

