/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseAggregator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class PendingWriteQueue {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
    private final ChannelHandlerContext ctx;
    private final ChannelOutboundBuffer buffer;
    private final MessageSizeEstimator.Handle estimatorHandle;
    private PendingWrite head;
    private PendingWrite tail;
    private int size;

    public PendingWriteQueue(ChannelHandlerContext ctx) {
        if (ctx == null) {
            throw new NullPointerException("ctx");
        }
        this.ctx = ctx;
        this.buffer = ctx.channel().unsafe().outboundBuffer();
        this.estimatorHandle = ctx.channel().config().getMessageSizeEstimator().newHandle();
    }

    public boolean isEmpty() {
        assert (this.ctx.executor().inEventLoop());
        return this.head == null;
    }

    public int size() {
        assert (this.ctx.executor().inEventLoop());
        return this.size;
    }

    public void add(Object msg, ChannelPromise promise) {
        assert (this.ctx.executor().inEventLoop());
        if (msg == null) {
            throw new NullPointerException("msg");
        }
        if (promise == null) {
            throw new NullPointerException("promise");
        }
        int messageSize = this.estimatorHandle.size(msg);
        if (messageSize < 0) {
            messageSize = 0;
        }
        PendingWrite write = PendingWrite.newInstance(msg, messageSize, promise);
        PendingWrite currentTail = this.tail;
        if (currentTail == null) {
            this.tail = this.head = write;
        } else {
            currentTail.next = write;
            this.tail = write;
        }
        ++this.size;
        this.buffer.incrementPendingOutboundBytes(write.size);
    }

    public void removeAndFailAll(Throwable cause) {
        assert (this.ctx.executor().inEventLoop());
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        PendingWrite write = this.head;
        while (write != null) {
            PendingWrite next = write.next;
            ReferenceCountUtil.safeRelease(write.msg);
            ChannelPromise promise = write.promise;
            this.recycle(write);
            PendingWriteQueue.safeFail(promise, cause);
            write = next;
        }
        this.assertEmpty();
    }

    public void removeAndFail(Throwable cause) {
        assert (this.ctx.executor().inEventLoop());
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        PendingWrite write = this.head;
        if (write == null) {
            return;
        }
        ReferenceCountUtil.safeRelease(write.msg);
        ChannelPromise promise = write.promise;
        PendingWriteQueue.safeFail(promise, cause);
        this.recycle(write);
    }

    public ChannelFuture removeAndWriteAll() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        if (this.size == 1) {
            return this.removeAndWrite();
        }
        ChannelPromise p = this.ctx.newPromise();
        ChannelPromiseAggregator aggregator = new ChannelPromiseAggregator(p);
        while (write != null) {
            PendingWrite next = write.next;
            Object msg = write.msg;
            ChannelPromise promise = write.promise;
            this.recycle(write);
            this.ctx.write(msg, promise);
            aggregator.add(promise);
            write = next;
        }
        this.assertEmpty();
        return p;
    }

    private void assertEmpty() {
        assert (this.tail == null && this.head == null && this.size == 0);
    }

    public ChannelFuture removeAndWrite() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        Object msg = write.msg;
        ChannelPromise promise = write.promise;
        this.recycle(write);
        return this.ctx.write(msg, promise);
    }

    public ChannelPromise remove() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        ChannelPromise promise = write.promise;
        ReferenceCountUtil.safeRelease(write.msg);
        this.recycle(write);
        return promise;
    }

    public Object current() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        return write.msg;
    }

    private void recycle(PendingWrite write) {
        PendingWrite next = write.next;
        this.buffer.decrementPendingOutboundBytes(write.size);
        write.recycle();
        --this.size;
        if (next == null) {
            this.tail = null;
            this.head = null;
            assert (this.size == 0);
        } else {
            this.head = next;
            assert (this.size > 0);
        }
    }

    private static void safeFail(ChannelPromise promise, Throwable cause) {
        if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
            logger.warn("Failed to mark a promise as failure because it's done already: {}", (Object)promise, (Object)cause);
        }
    }

    static final class PendingWrite {
        private static final Recycler<PendingWrite> RECYCLER = new Recycler<PendingWrite>(){

            @Override
            protected PendingWrite newObject(Recycler.Handle handle) {
                return new PendingWrite(handle);
            }
        };
        private final Recycler.Handle handle;
        private PendingWrite next;
        private long size;
        private ChannelPromise promise;
        private Object msg;

        private PendingWrite(Recycler.Handle handle) {
            this.handle = handle;
        }

        static PendingWrite newInstance(Object msg, int size, ChannelPromise promise) {
            PendingWrite write = RECYCLER.get();
            write.size = size;
            write.msg = msg;
            write.promise = promise;
            return write;
        }

        private void recycle() {
            this.size = 0L;
            this.next = null;
            this.msg = null;
            this.promise = null;
            RECYCLER.recycle(this, this.handle);
        }
    }
}

