/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel
extends AbstractNioChannel {
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';
    private Runnable flushTask;

    protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super(parent, ch, 1);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioByteUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        block11: {
            block12: {
                boolean setOpWrite;
                int writeSpinCount = -1;
                while (true) {
                    Object msg;
                    if ((msg = in.current()) == null) {
                        this.clearOpWrite();
                        break block11;
                    }
                    if (msg instanceof ByteBuf) {
                        ByteBuf buf = (ByteBuf)msg;
                        int readableBytes = buf.readableBytes();
                        if (readableBytes == 0) {
                            in.remove();
                            continue;
                        }
                        boolean setOpWrite2 = false;
                        boolean done = false;
                        long flushedAmount = 0L;
                        if (writeSpinCount == -1) {
                            writeSpinCount = this.config().getWriteSpinCount();
                        }
                        for (int i = writeSpinCount - 1; i >= 0; --i) {
                            int localFlushedAmount = this.doWriteBytes(buf);
                            if (localFlushedAmount == 0) {
                                setOpWrite2 = true;
                                break;
                            }
                            flushedAmount += (long)localFlushedAmount;
                            if (buf.isReadable()) continue;
                            done = true;
                            break;
                        }
                        in.progress(flushedAmount);
                        if (done) {
                            in.remove();
                            continue;
                        }
                        this.incompleteWrite(setOpWrite2);
                        break block11;
                    }
                    if (!(msg instanceof FileRegion)) break block12;
                    FileRegion region = (FileRegion)msg;
                    setOpWrite = false;
                    boolean done = false;
                    long flushedAmount = 0L;
                    if (writeSpinCount == -1) {
                        writeSpinCount = this.config().getWriteSpinCount();
                    }
                    for (int i = writeSpinCount - 1; i >= 0; --i) {
                        long localFlushedAmount = this.doWriteFileRegion(region);
                        if (localFlushedAmount == 0L) {
                            setOpWrite = true;
                            break;
                        }
                        flushedAmount += localFlushedAmount;
                        if (region.transfered() < region.count()) continue;
                        done = true;
                        break;
                    }
                    in.progress(flushedAmount);
                    if (!done) break;
                    in.remove();
                }
                this.incompleteWrite(setOpWrite);
                break block11;
            }
            throw new Error();
        }
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (buf.isDirect()) {
                return msg;
            }
            return this.newDirectBuffer(buf);
        }
        if (msg instanceof FileRegion) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    protected final void incompleteWrite(boolean setOpWrite) {
        if (setOpWrite) {
            this.setOpWrite();
        } else {
            Runnable flushTask = this.flushTask;
            if (flushTask == null) {
                flushTask = this.flushTask = new Runnable(){

                    @Override
                    public void run() {
                        AbstractNioByteChannel.this.flush();
                    }
                };
            }
            this.eventLoop().execute(flushTask);
        }
    }

    protected abstract long doWriteFileRegion(FileRegion var1) throws Exception;

    protected abstract int doReadBytes(ByteBuf var1) throws Exception;

    protected abstract int doWriteBytes(ByteBuf var1) throws Exception;

    protected final void setOpWrite() {
        SelectionKey key = this.selectionKey();
        if (!key.isValid()) {
            return;
        }
        int interestOps = key.interestOps();
        if ((interestOps & 4) == 0) {
            key.interestOps(interestOps | 4);
        }
    }

    protected final void clearOpWrite() {
        SelectionKey key = this.selectionKey();
        if (!key.isValid()) {
            return;
        }
        int interestOps = key.interestOps();
        if ((interestOps & 4) != 0) {
            key.interestOps(interestOps & 0xFFFFFFFB);
        }
    }

    private final class NioByteUnsafe
    extends AbstractNioChannel.AbstractNioUnsafe {
        private RecvByteBufAllocator.Handle allocHandle;

        private NioByteUnsafe() {
            super(AbstractNioByteChannel.this);
        }

        private void closeOnRead(ChannelPipeline pipeline) {
            SelectionKey key = AbstractNioByteChannel.this.selectionKey();
            AbstractNioByteChannel.this.setInputShutdown();
            if (AbstractNioByteChannel.this.isOpen()) {
                if (Boolean.TRUE.equals(AbstractNioByteChannel.this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                    key.interestOps(key.interestOps() & ~AbstractNioByteChannel.this.readInterestOp);
                    pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                } else {
                    this.close(this.voidPromise());
                }
            }
        }

        private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    AbstractNioByteChannel.this.setReadPending(false);
                    pipeline.fireChannelRead(byteBuf);
                } else {
                    byteBuf.release();
                }
            }
            pipeline.fireChannelReadComplete();
            pipeline.fireExceptionCaught(cause);
            if (close || cause instanceof IOException) {
                this.closeOnRead(pipeline);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void read() {
            ChannelConfig config = AbstractNioByteChannel.this.config();
            if (!config.isAutoRead() && !AbstractNioByteChannel.this.isReadPending()) {
                this.removeReadOp();
                return;
            }
            ChannelPipeline pipeline = AbstractNioByteChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            int maxMessagesPerRead = config.getMaxMessagesPerRead();
            RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
            if (allocHandle == null) {
                this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
            }
            ByteBuf byteBuf = null;
            int messages = 0;
            boolean close = false;
            try {
                int writable;
                int localReadAmount;
                int totalReadAmount = 0;
                boolean readPendingReset = false;
                do {
                    byteBuf = allocHandle.allocate(allocator);
                    writable = byteBuf.writableBytes();
                    localReadAmount = AbstractNioByteChannel.this.doReadBytes(byteBuf);
                    if (localReadAmount <= 0) {
                        byteBuf.release();
                        close = localReadAmount < 0;
                        break;
                    }
                    if (!readPendingReset) {
                        readPendingReset = true;
                        AbstractNioByteChannel.this.setReadPending(false);
                    }
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;
                    if (totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                        totalReadAmount = Integer.MAX_VALUE;
                        break;
                    }
                    totalReadAmount += localReadAmount;
                } while (config.isAutoRead() && localReadAmount >= writable && ++messages < maxMessagesPerRead);
                pipeline.fireChannelReadComplete();
                allocHandle.record(totalReadAmount);
                if (close) {
                    this.closeOnRead(pipeline);
                    close = false;
                }
            }
            catch (Throwable t) {
                this.handleReadException(pipeline, byteBuf, t, close);
            }
            finally {
                if (!config.isAutoRead() && !AbstractNioByteChannel.this.isReadPending()) {
                    this.removeReadOp();
                }
            }
        }
    }
}

