/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioChannel;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

public abstract class AbstractOioByteChannel
extends AbstractOioChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';
    private RecvByteBufAllocator.Handle allocHandle;
    private volatile boolean inputShutdown;

    protected AbstractOioByteChannel(Channel parent) {
        super(parent);
    }

    protected boolean isInputShutdown() {
        return this.inputShutdown;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    protected boolean checkInputShutdown() {
        if (this.inputShutdown) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doRead() {
        if (this.checkInputShutdown()) {
            return;
        }
        ChannelConfig config = this.config();
        ChannelPipeline pipeline = this.pipeline();
        RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
        if (allocHandle == null) {
            this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
        }
        ByteBuf byteBuf = allocHandle.allocate(this.alloc());
        boolean closed = false;
        boolean read = false;
        Throwable exception = null;
        int localReadAmount = 0;
        try {
            int totalReadAmount = 0;
            do {
                if ((localReadAmount = this.doReadBytes(byteBuf)) > 0) {
                    read = true;
                } else if (localReadAmount < 0) {
                    closed = true;
                }
                int available = this.available();
                if (available <= 0) break;
                if (!byteBuf.isWritable()) {
                    int maxCapacity;
                    int capacity = byteBuf.capacity();
                    if (capacity == (maxCapacity = byteBuf.maxCapacity())) {
                        if (read) {
                            read = false;
                            pipeline.fireChannelRead(byteBuf);
                            byteBuf = this.alloc().buffer();
                        }
                    } else {
                        int writerIndex = byteBuf.writerIndex();
                        if (writerIndex + available > maxCapacity) {
                            byteBuf.capacity(maxCapacity);
                        } else {
                            byteBuf.ensureWritable(available);
                        }
                    }
                }
                if (totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                    totalReadAmount = Integer.MAX_VALUE;
                    break;
                }
                totalReadAmount += localReadAmount;
            } while (config.isAutoRead());
            allocHandle.record(totalReadAmount);
        }
        catch (Throwable t) {
            exception = t;
        }
        finally {
            if (read) {
                pipeline.fireChannelRead(byteBuf);
            } else {
                byteBuf.release();
            }
            pipeline.fireChannelReadComplete();
            if (exception != null) {
                if (exception instanceof IOException) {
                    closed = true;
                    this.pipeline().fireExceptionCaught(exception);
                } else {
                    pipeline.fireExceptionCaught(exception);
                    this.unsafe().close(this.voidPromise());
                }
            }
            if (closed) {
                this.inputShutdown = true;
                if (this.isOpen()) {
                    if (Boolean.TRUE.equals(this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                        pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                    } else {
                        this.unsafe().close(this.unsafe().voidPromise());
                    }
                }
            }
            if (localReadAmount == 0 && this.isActive()) {
                this.read();
            }
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        while ((msg = in.current()) != null) {
            if (msg instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf)msg;
                int readableBytes = buf.readableBytes();
                while (readableBytes > 0) {
                    this.doWriteBytes(buf);
                    int newReadableBytes = buf.readableBytes();
                    in.progress(readableBytes - newReadableBytes);
                    readableBytes = newReadableBytes;
                }
                in.remove();
                continue;
            }
            if (msg instanceof FileRegion) {
                FileRegion region = (FileRegion)msg;
                long transfered = region.transfered();
                this.doWriteFileRegion(region);
                in.progress(region.transfered() - transfered);
                in.remove();
                continue;
            }
            in.remove(new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg)));
        }
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) throws Exception {
        if (msg instanceof ByteBuf || msg instanceof FileRegion) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    protected abstract int available();

    protected abstract int doReadBytes(ByteBuf var1) throws Exception;

    protected abstract void doWriteBytes(ByteBuf var1) throws Exception;

    protected abstract void doWriteFileRegion(FileRegion var1) throws Exception;
}

