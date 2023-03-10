/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollSocketChannelConfig;
import io.netty.channel.epoll.IovArray;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class EpollSocketChannel
extends AbstractEpollChannel
implements SocketChannel {
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
    private final EpollSocketChannelConfig config = new EpollSocketChannelConfig(this);
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    private volatile InetSocketAddress local;
    private volatile InetSocketAddress remote;
    private volatile boolean inputShutdown;
    private volatile boolean outputShutdown;

    EpollSocketChannel(Channel parent, int fd) {
        super(parent, fd, 1, true);
        this.remote = Native.remoteAddress(fd);
        this.local = Native.localAddress(fd);
    }

    public EpollSocketChannel() {
        super(Native.socketStreamFd(), 1);
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollSocketUnsafe();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remote;
    }

    @Override
    protected void doBind(SocketAddress local) throws Exception {
        InetSocketAddress localAddress = (InetSocketAddress)local;
        Native.bind(this.fd, localAddress.getAddress(), localAddress.getPort());
        this.local = Native.localAddress(this.fd);
    }

    private boolean writeBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            in.remove();
            return true;
        }
        boolean done = false;
        long writtenBytes = 0L;
        if (buf.hasMemoryAddress()) {
            block6: {
                int localFlushedAmount;
                long memoryAddress = buf.memoryAddress();
                int readerIndex = buf.readerIndex();
                int writerIndex = buf.writerIndex();
                while ((localFlushedAmount = Native.writeAddress(this.fd, memoryAddress, readerIndex, writerIndex)) > 0) {
                    if ((writtenBytes += (long)localFlushedAmount) == (long)readableBytes) {
                        done = true;
                        break block6;
                    }
                    readerIndex += localFlushedAmount;
                }
                this.setEpollOut();
            }
            in.removeBytes(writtenBytes);
            return done;
        }
        if (buf.nioBufferCount() == 1) {
            block7: {
                int limit;
                int pos;
                int localFlushedAmount;
                int readerIndex = buf.readerIndex();
                ByteBuffer nioBuf = buf.internalNioBuffer(readerIndex, buf.readableBytes());
                while ((localFlushedAmount = Native.write(this.fd, nioBuf, pos = nioBuf.position(), limit = nioBuf.limit())) > 0) {
                    nioBuf.position(pos + localFlushedAmount);
                    if ((writtenBytes += (long)localFlushedAmount) != (long)readableBytes) continue;
                    done = true;
                    break block7;
                }
                this.setEpollOut();
            }
            in.removeBytes(writtenBytes);
            return done;
        }
        ByteBuffer[] nioBuffers = buf.nioBuffers();
        return this.writeBytesMultiple(in, nioBuffers, nioBuffers.length, readableBytes);
    }

    private boolean writeBytesMultiple(ChannelOutboundBuffer in, IovArray array) throws IOException {
        long expectedWrittenBytes = array.size();
        int cnt = array.count();
        assert (expectedWrittenBytes != 0L);
        assert (cnt != 0);
        boolean done = false;
        long writtenBytes = 0L;
        int offset = 0;
        int end = offset + cnt;
        block0: while (true) {
            long bytes;
            long localWrittenBytes;
            if ((localWrittenBytes = Native.writevAddresses(this.fd, array.memoryAddress(offset), cnt)) == 0L) {
                this.setEpollOut();
                break;
            }
            writtenBytes += localWrittenBytes;
            if ((expectedWrittenBytes -= localWrittenBytes) == 0L) {
                done = true;
                break;
            }
            do {
                if ((bytes = array.processWritten(offset, localWrittenBytes)) == -1L) continue block0;
                --cnt;
            } while (++offset < end && (localWrittenBytes -= bytes) > 0L);
        }
        in.removeBytes(writtenBytes);
        return done;
    }

    private boolean writeBytesMultiple(ChannelOutboundBuffer in, ByteBuffer[] nioBuffers, int nioBufferCnt, long expectedWrittenBytes) throws IOException {
        assert (expectedWrittenBytes != 0L);
        boolean done = false;
        long writtenBytes = 0L;
        int offset = 0;
        int end = offset + nioBufferCnt;
        block0: while (true) {
            int bytes;
            long localWrittenBytes;
            if ((localWrittenBytes = Native.writev(this.fd, nioBuffers, offset, nioBufferCnt)) == 0L) {
                this.setEpollOut();
                break;
            }
            writtenBytes += localWrittenBytes;
            if ((expectedWrittenBytes -= localWrittenBytes) == 0L) {
                done = true;
                break;
            }
            do {
                ByteBuffer buffer = nioBuffers[offset];
                int pos = buffer.position();
                bytes = buffer.limit() - pos;
                if ((long)bytes > localWrittenBytes) {
                    buffer.position(pos + (int)localWrittenBytes);
                    continue block0;
                }
                --nioBufferCnt;
            } while (++offset < end && (localWrittenBytes -= (long)bytes) > 0L);
        }
        in.removeBytes(writtenBytes);
        return done;
    }

    private boolean writeFileRegion(ChannelOutboundBuffer in, DefaultFileRegion region) throws Exception {
        long regionCount = region.count();
        if (region.transfered() >= regionCount) {
            in.remove();
            return true;
        }
        long baseOffset = region.position();
        boolean done = false;
        long flushedAmount = 0L;
        for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
            long offset = region.transfered();
            long localFlushedAmount = Native.sendfile(this.fd, region, baseOffset, offset, regionCount - offset);
            if (localFlushedAmount == 0L) {
                this.setEpollOut();
                break;
            }
            flushedAmount += localFlushedAmount;
            if (region.transfered() < regionCount) continue;
            done = true;
            break;
        }
        if (flushedAmount > 0L) {
            in.progress(flushedAmount);
        }
        if (done) {
            in.remove();
        }
        return done;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        int msgCount;
        do {
            if ((msgCount = in.size()) != 0) continue;
            this.clearEpollOut();
            break;
        } while (!(msgCount > 1 && in.current() instanceof ByteBuf ? !this.doWriteMultiple(in) : !this.doWriteSingle(in)));
    }

    private boolean doWriteSingle(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (!this.writeBytes(in, buf)) {
                return false;
            }
        } else if (msg instanceof DefaultFileRegion) {
            DefaultFileRegion region = (DefaultFileRegion)msg;
            if (!this.writeFileRegion(in, region)) {
                return false;
            }
        } else {
            throw new Error();
        }
        return true;
    }

    private boolean doWriteMultiple(ChannelOutboundBuffer in) throws Exception {
        if (PlatformDependent.hasUnsafe()) {
            IovArray array = IovArray.get(in);
            int cnt = array.count();
            if (cnt >= 1) {
                if (!this.writeBytesMultiple(in, array)) {
                    return false;
                }
            } else {
                in.removeBytes(0L);
            }
        } else {
            ByteBuffer[] buffers = in.nioBuffers();
            int cnt = in.nioBufferCount();
            if (cnt >= 1) {
                if (!this.writeBytesMultiple(in, buffers, cnt, in.nioBufferSize())) {
                    return false;
                }
            } else {
                in.removeBytes(0L);
            }
        }
        return true;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (!(buf.hasMemoryAddress() || !PlatformDependent.hasUnsafe() && buf.isDirect())) {
                buf = this.newDirectBuffer(buf);
                assert (buf.hasMemoryAddress());
            }
            return buf;
        }
        if (msg instanceof DefaultFileRegion) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    @Override
    public EpollSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isInputShutdown() {
        return this.inputShutdown;
    }

    @Override
    public boolean isOutputShutdown() {
        return this.outputShutdown || !this.isActive();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            try {
                Native.shutdown(this.fd, false, true);
                this.outputShutdown = true;
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            loop.execute(new Runnable(){

                @Override
                public void run() {
                    EpollSocketChannel.this.shutdownOutput(promise);
                }
            });
        }
        return promise;
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    final class EpollSocketUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        private RecvByteBufAllocator.Handle allocHandle;

        EpollSocketUnsafe() {
        }

        private void closeOnRead(ChannelPipeline pipeline) {
            EpollSocketChannel.this.inputShutdown = true;
            if (EpollSocketChannel.this.isOpen()) {
                if (Boolean.TRUE.equals(EpollSocketChannel.this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                    this.clearEpollIn0();
                    pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                } else {
                    this.close(this.voidPromise());
                }
            }
        }

        private boolean handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                } else {
                    byteBuf.release();
                }
            }
            pipeline.fireChannelReadComplete();
            pipeline.fireExceptionCaught(cause);
            if (close || cause instanceof IOException) {
                this.closeOnRead(pipeline);
                return true;
            }
            return false;
        }

        @Override
        public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                if (EpollSocketChannel.this.connectPromise != null) {
                    throw new IllegalStateException("connection attempt already made");
                }
                boolean wasActive = EpollSocketChannel.this.isActive();
                if (this.doConnect((InetSocketAddress)remoteAddress, (InetSocketAddress)localAddress)) {
                    this.fulfillConnectPromise(promise, wasActive);
                } else {
                    EpollSocketChannel.this.connectPromise = promise;
                    EpollSocketChannel.this.requestedRemoteAddress = remoteAddress;
                    int connectTimeoutMillis = EpollSocketChannel.this.config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        EpollSocketChannel.this.connectTimeoutFuture = EpollSocketChannel.this.eventLoop().schedule(new Runnable(){

                            @Override
                            public void run() {
                                ChannelPromise connectPromise = EpollSocketChannel.this.connectPromise;
                                ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                                if (connectPromise != null && connectPromise.tryFailure(cause)) {
                                    EpollSocketUnsafe.this.close(EpollSocketUnsafe.this.voidPromise());
                                }
                            }
                        }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                    }
                    promise.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isCancelled()) {
                                if (EpollSocketChannel.this.connectTimeoutFuture != null) {
                                    EpollSocketChannel.this.connectTimeoutFuture.cancel(false);
                                }
                                EpollSocketChannel.this.connectPromise = null;
                                EpollSocketUnsafe.this.close(EpollSocketUnsafe.this.voidPromise());
                            }
                        }
                    });
                }
            }
            catch (Throwable t2) {
                ConnectException t2;
                if (t2 instanceof ConnectException) {
                    ConnectException newT = new ConnectException(t2.getMessage() + ": " + remoteAddress);
                    newT.setStackTrace(t2.getStackTrace());
                    t2 = newT;
                }
                this.closeIfClosed();
                promise.tryFailure(t2);
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
            if (promise == null) {
                return;
            }
            EpollSocketChannel.this.active = true;
            boolean promiseSet = promise.trySuccess();
            if (!wasActive && EpollSocketChannel.this.isActive()) {
                EpollSocketChannel.this.pipeline().fireChannelActive();
            }
            if (!promiseSet) {
                this.close(this.voidPromise());
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
            if (promise == null) {
                return;
            }
            promise.tryFailure(cause);
            this.closeIfClosed();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void finishConnect() {
            assert (EpollSocketChannel.this.eventLoop().inEventLoop());
            boolean connectStillInProgress = false;
            try {
                boolean wasActive = EpollSocketChannel.this.isActive();
                if (!this.doFinishConnect()) {
                    connectStillInProgress = true;
                    return;
                }
                this.fulfillConnectPromise(EpollSocketChannel.this.connectPromise, wasActive);
            }
            catch (Throwable t2) {
                ConnectException t2;
                if (t2 instanceof ConnectException) {
                    ConnectException newT = new ConnectException(t2.getMessage() + ": " + EpollSocketChannel.this.requestedRemoteAddress);
                    newT.setStackTrace(t2.getStackTrace());
                    t2 = newT;
                }
                this.fulfillConnectPromise(EpollSocketChannel.this.connectPromise, t2);
            }
            finally {
                if (!connectStillInProgress) {
                    if (EpollSocketChannel.this.connectTimeoutFuture != null) {
                        EpollSocketChannel.this.connectTimeoutFuture.cancel(false);
                    }
                    EpollSocketChannel.this.connectPromise = null;
                }
            }
        }

        @Override
        void epollOutReady() {
            if (EpollSocketChannel.this.connectPromise != null) {
                this.finishConnect();
            } else {
                super.epollOutReady();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean doConnect(InetSocketAddress remoteAddress, InetSocketAddress localAddress) throws Exception {
            if (localAddress != null) {
                AbstractEpollChannel.checkResolvable(localAddress);
                Native.bind(EpollSocketChannel.this.fd, localAddress.getAddress(), localAddress.getPort());
            }
            boolean success = false;
            try {
                AbstractEpollChannel.checkResolvable(remoteAddress);
                boolean connected = Native.connect(EpollSocketChannel.this.fd, remoteAddress.getAddress(), remoteAddress.getPort());
                EpollSocketChannel.this.remote = remoteAddress;
                EpollSocketChannel.this.local = Native.localAddress(EpollSocketChannel.this.fd);
                if (!connected) {
                    EpollSocketChannel.this.setEpollOut();
                }
                success = true;
                boolean bl = connected;
                return bl;
            }
            finally {
                if (!success) {
                    EpollSocketChannel.this.doClose();
                }
            }
        }

        private boolean doFinishConnect() throws Exception {
            if (Native.finishConnect(EpollSocketChannel.this.fd)) {
                EpollSocketChannel.this.clearEpollOut();
                return true;
            }
            EpollSocketChannel.this.setEpollOut();
            return false;
        }

        private int doReadBytes(ByteBuf byteBuf) throws Exception {
            int localReadAmount;
            int writerIndex = byteBuf.writerIndex();
            if (byteBuf.hasMemoryAddress()) {
                localReadAmount = Native.readAddress(EpollSocketChannel.this.fd, byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
            } else {
                ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
                localReadAmount = Native.read(EpollSocketChannel.this.fd, buf, buf.position(), buf.limit());
            }
            if (localReadAmount > 0) {
                byteBuf.writerIndex(writerIndex + localReadAmount);
            }
            return localReadAmount;
        }

        @Override
        void epollRdHupReady() {
            if (EpollSocketChannel.this.isActive()) {
                this.epollInReady();
            } else {
                this.closeOnRead(EpollSocketChannel.this.pipeline());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void epollInReady() {
            EpollSocketChannelConfig config = EpollSocketChannel.this.config();
            ChannelPipeline pipeline = EpollSocketChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
            if (allocHandle == null) {
                this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
            }
            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                int writable;
                int localReadAmount;
                int totalReadAmount = 0;
                do {
                    byteBuf = allocHandle.allocate(allocator);
                    writable = byteBuf.writableBytes();
                    localReadAmount = this.doReadBytes(byteBuf);
                    if (localReadAmount <= 0) {
                        byteBuf.release();
                        close = localReadAmount < 0;
                        break;
                    }
                    this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;
                    if (totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                        allocHandle.record(totalReadAmount);
                        totalReadAmount = localReadAmount;
                        continue;
                    }
                    totalReadAmount += localReadAmount;
                } while (localReadAmount >= writable);
                pipeline.fireChannelReadComplete();
                allocHandle.record(totalReadAmount);
                if (close) {
                    this.closeOnRead(pipeline);
                    close = false;
                }
            }
            catch (Throwable t) {
                boolean closed = this.handleReadException(pipeline, byteBuf, t, close);
                if (!closed) {
                    EpollSocketChannel.this.eventLoop().execute(new Runnable(){

                        @Override
                        public void run() {
                            EpollSocketUnsafe.this.epollInReady();
                        }
                    });
                }
            }
            finally {
                if (!config.isAutoRead() && !this.readPending) {
                    this.clearEpollIn0();
                }
            }
        }
    }
}

