/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ChannelOutboundBuffer {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
    private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS = new FastThreadLocal<ByteBuffer[]>(){

        @Override
        protected ByteBuffer[] initialValue() throws Exception {
            return new ByteBuffer[1024];
        }
    };
    private final Channel channel;
    private Entry flushedEntry;
    private Entry unflushedEntry;
    private Entry tailEntry;
    private int flushed;
    private int nioBufferCount;
    private long nioBufferSize;
    private boolean inFail;
    private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER;
    private volatile long totalPendingSize;
    private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> WRITABLE_UPDATER;
    private volatile int writable = 1;

    ChannelOutboundBuffer(AbstractChannel channel) {
        this.channel = channel;
    }

    public void addMessage(Object msg, int size, ChannelPromise promise) {
        Entry entry = Entry.newInstance(msg, size, ChannelOutboundBuffer.total(msg), promise);
        if (this.tailEntry == null) {
            this.flushedEntry = null;
            this.tailEntry = entry;
        } else {
            Entry tail = this.tailEntry;
            tail.next = entry;
            this.tailEntry = entry;
        }
        if (this.unflushedEntry == null) {
            this.unflushedEntry = entry;
        }
        this.incrementPendingOutboundBytes(size);
    }

    public void addFlush() {
        Entry entry = this.unflushedEntry;
        if (entry != null) {
            if (this.flushedEntry == null) {
                this.flushedEntry = entry;
            }
            do {
                ++this.flushed;
                if (entry.promise.setUncancellable()) continue;
                int pending = entry.cancel();
                this.decrementPendingOutboundBytes(pending);
            } while ((entry = entry.next) != null);
            this.unflushedEntry = null;
        }
    }

    void incrementPendingOutboundBytes(long size) {
        if (size == 0L) {
            return;
        }
        long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
        if (newWriteBufferSize > (long)this.channel.config().getWriteBufferHighWaterMark() && WRITABLE_UPDATER.compareAndSet(this, 1, 0)) {
            this.channel.pipeline().fireChannelWritabilityChanged();
        }
    }

    void decrementPendingOutboundBytes(long size) {
        if (size == 0L) {
            return;
        }
        long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
        if ((newWriteBufferSize == 0L || newWriteBufferSize < (long)this.channel.config().getWriteBufferLowWaterMark()) && WRITABLE_UPDATER.compareAndSet(this, 0, 1)) {
            this.channel.pipeline().fireChannelWritabilityChanged();
        }
    }

    private static long total(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf)msg).readableBytes();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion)msg).count();
        }
        if (msg instanceof ByteBufHolder) {
            return ((ByteBufHolder)msg).content().readableBytes();
        }
        return -1L;
    }

    public Object current() {
        Entry entry = this.flushedEntry;
        if (entry == null) {
            return null;
        }
        return entry.msg;
    }

    public void progress(long amount) {
        Entry e = this.flushedEntry;
        assert (e != null);
        ChannelPromise p = e.promise;
        if (p instanceof ChannelProgressivePromise) {
            long progress;
            e.progress = progress = e.progress + amount;
            ((ChannelProgressivePromise)p).tryProgress(progress, e.total);
        }
    }

    public boolean remove() {
        Entry e = this.flushedEntry;
        if (e == null) {
            return false;
        }
        Object msg = e.msg;
        ChannelPromise promise = e.promise;
        int size = e.pendingSize;
        this.removeEntry(e);
        if (!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            ChannelOutboundBuffer.safeSuccess(promise);
            this.decrementPendingOutboundBytes(size);
        }
        e.recycle();
        return true;
    }

    public boolean remove(Throwable cause) {
        Entry e = this.flushedEntry;
        if (e == null) {
            return false;
        }
        Object msg = e.msg;
        ChannelPromise promise = e.promise;
        int size = e.pendingSize;
        this.removeEntry(e);
        if (!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            ChannelOutboundBuffer.safeFail(promise, cause);
            this.decrementPendingOutboundBytes(size);
        }
        e.recycle();
        return true;
    }

    private void removeEntry(Entry e) {
        if (--this.flushed == 0) {
            this.flushedEntry = null;
            if (e == this.tailEntry) {
                this.tailEntry = null;
                this.unflushedEntry = null;
            }
        } else {
            this.flushedEntry = e.next;
        }
    }

    public void removeBytes(long writtenBytes) {
        block4: {
            int readerIndex;
            ByteBuf buf;
            while (true) {
                Object msg;
                if (!((msg = this.current()) instanceof ByteBuf)) {
                    assert (writtenBytes == 0L);
                    break block4;
                }
                buf = (ByteBuf)msg;
                readerIndex = buf.readerIndex();
                int readableBytes = buf.writerIndex() - readerIndex;
                if ((long)readableBytes > writtenBytes) break;
                if (writtenBytes != 0L) {
                    this.progress(readableBytes);
                    writtenBytes -= (long)readableBytes;
                }
                this.remove();
            }
            if (writtenBytes == 0L) break block4;
            buf.readerIndex(readerIndex + (int)writtenBytes);
            this.progress(writtenBytes);
        }
    }

    public ByteBuffer[] nioBuffers() {
        long nioBufferSize = 0L;
        int nioBufferCount = 0;
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
        ByteBuffer[] nioBuffers = NIO_BUFFERS.get(threadLocalMap);
        Entry entry = this.flushedEntry;
        while (this.isFlushedEntry(entry) && entry.msg instanceof ByteBuf) {
            if (!entry.cancelled) {
                ByteBuf buf = (ByteBuf)entry.msg;
                int readerIndex = buf.readerIndex();
                int readableBytes = buf.writerIndex() - readerIndex;
                if (readableBytes > 0) {
                    int neededSpace;
                    nioBufferSize += (long)readableBytes;
                    int count = entry.count;
                    if (count == -1) {
                        entry.count = count = buf.nioBufferCount();
                    }
                    if ((neededSpace = nioBufferCount + count) > nioBuffers.length) {
                        nioBuffers = ChannelOutboundBuffer.expandNioBufferArray(nioBuffers, neededSpace, nioBufferCount);
                        NIO_BUFFERS.set(threadLocalMap, nioBuffers);
                    }
                    if (count == 1) {
                        ByteBuffer nioBuf = entry.buf;
                        if (nioBuf == null) {
                            entry.buf = nioBuf = buf.internalNioBuffer(readerIndex, readableBytes);
                        }
                        nioBuffers[nioBufferCount++] = nioBuf;
                    } else {
                        ByteBuffer[] nioBufs = entry.bufs;
                        if (nioBufs == null) {
                            nioBufs = buf.nioBuffers();
                            entry.bufs = nioBufs;
                        }
                        nioBufferCount = ChannelOutboundBuffer.fillBufferArray(nioBufs, nioBuffers, nioBufferCount);
                    }
                }
            }
            entry = entry.next;
        }
        this.nioBufferCount = nioBufferCount;
        this.nioBufferSize = nioBufferSize;
        return nioBuffers;
    }

    private static int fillBufferArray(ByteBuffer[] nioBufs, ByteBuffer[] nioBuffers, int nioBufferCount) {
        for (ByteBuffer nioBuf : nioBufs) {
            if (nioBuf == null) break;
            nioBuffers[nioBufferCount++] = nioBuf;
        }
        return nioBufferCount;
    }

    private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] array, int neededSpace, int size) {
        int newCapacity = array.length;
        do {
            if ((newCapacity <<= 1) >= 0) continue;
            throw new IllegalStateException();
        } while (neededSpace > newCapacity);
        ByteBuffer[] newArray = new ByteBuffer[newCapacity];
        System.arraycopy(array, 0, newArray, 0, size);
        return newArray;
    }

    public int nioBufferCount() {
        return this.nioBufferCount;
    }

    public long nioBufferSize() {
        return this.nioBufferSize;
    }

    boolean isWritable() {
        return this.writable != 0;
    }

    public int size() {
        return this.flushed;
    }

    public boolean isEmpty() {
        return this.flushed == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void failFlushed(Throwable cause) {
        if (this.inFail) {
            return;
        }
        try {
            this.inFail = true;
            while (this.remove(cause)) {
            }
        }
        finally {
            this.inFail = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close(final ClosedChannelException cause) {
        if (this.inFail) {
            this.channel.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    ChannelOutboundBuffer.this.close(cause);
                }
            });
            return;
        }
        this.inFail = true;
        if (this.channel.isOpen()) {
            throw new IllegalStateException("close() must be invoked after the channel is closed.");
        }
        if (!this.isEmpty()) {
            throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
        }
        try {
            for (Entry e = this.unflushedEntry; e != null; e = e.recycleAndGetNext()) {
                int size = e.pendingSize;
                TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
                if (e.cancelled) continue;
                ReferenceCountUtil.safeRelease(e.msg);
                ChannelOutboundBuffer.safeFail(e.promise, cause);
            }
        }
        finally {
            this.inFail = false;
        }
    }

    private static void safeSuccess(ChannelPromise promise) {
        if (!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
            logger.warn("Failed to mark a promise as success because it is done already: {}", (Object)promise);
        }
    }

    private static void safeFail(ChannelPromise promise, Throwable cause) {
        if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
            logger.warn("Failed to mark a promise as failure because it's done already: {}", (Object)promise, (Object)cause);
        }
    }

    @Deprecated
    public void recycle() {
    }

    public long totalPendingWriteBytes() {
        return this.totalPendingSize;
    }

    public void forEachFlushedMessage(MessageProcessor processor) throws Exception {
        if (processor == null) {
            throw new NullPointerException("processor");
        }
        Entry entry = this.flushedEntry;
        if (entry == null) {
            return;
        }
        do {
            if (entry.cancelled || processor.processMessage(entry.msg)) continue;
            return;
        } while (this.isFlushedEntry(entry = entry.next));
    }

    private boolean isFlushedEntry(Entry e) {
        return e != null && e != this.unflushedEntry;
    }

    static {
        AtomicIntegerFieldUpdater<Object> writableUpdater = PlatformDependent.newAtomicIntegerFieldUpdater(ChannelOutboundBuffer.class, "writable");
        if (writableUpdater == null) {
            writableUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "writable");
        }
        WRITABLE_UPDATER = writableUpdater;
        AtomicLongFieldUpdater<Object> pendingSizeUpdater = PlatformDependent.newAtomicLongFieldUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
        if (pendingSizeUpdater == null) {
            pendingSizeUpdater = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
        }
        TOTAL_PENDING_SIZE_UPDATER = pendingSizeUpdater;
    }

    static final class Entry {
        private static final Recycler<Entry> RECYCLER = new Recycler<Entry>(){

            @Override
            protected Entry newObject(Recycler.Handle handle) {
                return new Entry(handle);
            }
        };
        private final Recycler.Handle handle;
        Entry next;
        Object msg;
        ByteBuffer[] bufs;
        ByteBuffer buf;
        ChannelPromise promise;
        long progress;
        long total;
        int pendingSize;
        int count = -1;
        boolean cancelled;

        private Entry(Recycler.Handle handle) {
            this.handle = handle;
        }

        static Entry newInstance(Object msg, int size, long total, ChannelPromise promise) {
            Entry entry = RECYCLER.get();
            entry.msg = msg;
            entry.pendingSize = size;
            entry.total = total;
            entry.promise = promise;
            return entry;
        }

        int cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                int pSize = this.pendingSize;
                ReferenceCountUtil.safeRelease(this.msg);
                this.msg = Unpooled.EMPTY_BUFFER;
                this.pendingSize = 0;
                this.total = 0L;
                this.progress = 0L;
                this.bufs = null;
                this.buf = null;
                return pSize;
            }
            return 0;
        }

        void recycle() {
            this.next = null;
            this.bufs = null;
            this.buf = null;
            this.msg = null;
            this.promise = null;
            this.progress = 0L;
            this.total = 0L;
            this.pendingSize = 0;
            this.count = -1;
            this.cancelled = false;
            RECYCLER.recycle(this, this.handle);
        }

        Entry recycleAndGetNext() {
            Entry next = this.next;
            this.recycle();
            return next;
        }
    }

    public static interface MessageProcessor {
        public boolean processMessage(Object var1) throws Exception;
    }
}

