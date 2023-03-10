/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Recycler<T> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(Integer.MIN_VALUE);
    private static final int OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
    private static final int DEFAULT_MAX_CAPACITY;
    private static final int INITIAL_CAPACITY;
    private final int maxCapacity;
    private final FastThreadLocal<Stack<T>> threadLocal = new FastThreadLocal<Stack<T>>(){

        @Override
        protected Stack<T> initialValue() {
            return new Stack(Recycler.this, Thread.currentThread(), Recycler.this.maxCapacity);
        }
    };
    private static final FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED;

    protected Recycler() {
        this(DEFAULT_MAX_CAPACITY);
    }

    protected Recycler(int maxCapacity) {
        this.maxCapacity = Math.max(0, maxCapacity);
    }

    public final T get() {
        Stack<T> stack = this.threadLocal.get();
        DefaultHandle handle = stack.pop();
        if (handle == null) {
            handle = stack.newHandle();
            handle.value = this.newObject(handle);
        }
        return (T)handle.value;
    }

    public final boolean recycle(T o, Handle handle) {
        DefaultHandle h = (DefaultHandle)handle;
        if (((DefaultHandle)h).stack.parent != this) {
            return false;
        }
        if (o != h.value) {
            throw new IllegalArgumentException("o does not belong to handle");
        }
        h.recycle();
        return true;
    }

    protected abstract T newObject(Handle var1);

    static /* synthetic */ AtomicInteger access$400() {
        return ID_GENERATOR;
    }

    static {
        int maxCapacity = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity.default", 0);
        if (maxCapacity <= 0) {
            maxCapacity = 262144;
        }
        DEFAULT_MAX_CAPACITY = maxCapacity;
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.recycler.maxCapacity.default: {}", (Object)DEFAULT_MAX_CAPACITY);
        }
        INITIAL_CAPACITY = Math.min(DEFAULT_MAX_CAPACITY, 256);
        DELAYED_RECYCLED = new FastThreadLocal<Map<Stack<?>, WeakOrderQueue>>(){

            @Override
            protected Map<Stack<?>, WeakOrderQueue> initialValue() {
                return new WeakHashMap();
            }
        };
    }

    static final class Stack<T> {
        final Recycler<T> parent;
        final Thread thread;
        private DefaultHandle[] elements;
        private final int maxCapacity;
        private int size;
        private volatile WeakOrderQueue head;
        private WeakOrderQueue cursor;
        private WeakOrderQueue prev;

        Stack(Recycler<T> parent, Thread thread, int maxCapacity) {
            this.parent = parent;
            this.thread = thread;
            this.maxCapacity = maxCapacity;
            this.elements = new DefaultHandle[INITIAL_CAPACITY];
        }

        DefaultHandle pop() {
            DefaultHandle ret;
            int size = this.size;
            if (size == 0) {
                if (!this.scavenge()) {
                    return null;
                }
                size = this.size;
            }
            if ((ret = this.elements[--size]).lastRecycledId != ret.recycleId) {
                throw new IllegalStateException("recycled multiple times");
            }
            ret.recycleId = 0;
            ret.lastRecycledId = 0;
            this.size = size;
            return ret;
        }

        boolean scavenge() {
            if (this.scavengeSome()) {
                return true;
            }
            this.prev = null;
            this.cursor = this.head;
            return false;
        }

        boolean scavengeSome() {
            boolean success = false;
            WeakOrderQueue cursor = this.cursor;
            WeakOrderQueue prev = this.prev;
            while (cursor != null) {
                if (cursor.transfer(this)) {
                    success = true;
                    break;
                }
                WeakOrderQueue next = cursor.next;
                if (cursor.owner.get() == null) {
                    if (cursor.hasFinalData()) {
                        while (cursor.transfer(this)) {
                        }
                    }
                    if (prev != null) {
                        prev.next = next;
                    }
                } else {
                    prev = cursor;
                }
                cursor = next;
            }
            this.prev = prev;
            this.cursor = cursor;
            return success;
        }

        void push(DefaultHandle item) {
            if ((item.recycleId | item.lastRecycledId) != 0) {
                throw new IllegalStateException("recycled already");
            }
            item.recycleId = (item.lastRecycledId = OWN_THREAD_ID);
            int size = this.size;
            if (size == this.elements.length) {
                if (size == this.maxCapacity) {
                    return;
                }
                this.elements = Arrays.copyOf(this.elements, size << 1);
            }
            this.elements[size] = item;
            this.size = size + 1;
        }

        DefaultHandle newHandle() {
            return new DefaultHandle(this);
        }

        static /* synthetic */ DefaultHandle[] access$1202(Stack x0, DefaultHandle[] x1) {
            x0.elements = x1;
            return x1;
        }
    }

    private static final class WeakOrderQueue {
        private static final int LINK_CAPACITY = 16;
        private Link head;
        private Link tail;
        private WeakOrderQueue next;
        private final WeakReference<Thread> owner;
        private final int id = Recycler.access$400().getAndIncrement();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        WeakOrderQueue(Stack<?> stack, Thread thread) {
            this.head = this.tail = new Link();
            this.owner = new WeakReference<Thread>(thread);
            Stack<?> stack2 = stack;
            synchronized (stack2) {
                this.next = ((Stack)stack).head;
                ((Stack)stack).head = this;
            }
        }

        void add(DefaultHandle handle) {
            handle.lastRecycledId = this.id;
            Link tail = this.tail;
            int writeIndex = tail.get();
            if (writeIndex == 16) {
                this.tail = tail = (tail.next = new Link());
                writeIndex = tail.get();
            }
            ((Link)tail).elements[writeIndex] = handle;
            handle.stack = null;
            tail.lazySet(writeIndex + 1);
        }

        boolean hasFinalData() {
            return this.tail.readIndex != this.tail.get();
        }

        boolean transfer(Stack<?> to) {
            int end;
            int start;
            Link head = this.head;
            if (head == null) {
                return false;
            }
            if (head.readIndex == 16) {
                if (head.next == null) {
                    return false;
                }
                this.head = head = head.next;
            }
            if ((start = head.readIndex) == (end = head.get())) {
                return false;
            }
            int count = end - start;
            if (((Stack)to).size + count > ((Stack)to).elements.length) {
                Stack.access$1202(to, Arrays.copyOf(((Stack)to).elements, (((Stack)to).size + count) * 2));
            }
            DefaultHandle[] src = head.elements;
            DefaultHandle[] trg = ((Stack)to).elements;
            int size = ((Stack)to).size;
            while (start < end) {
                DefaultHandle element = src[start];
                if (element.recycleId == 0) {
                    element.recycleId = element.lastRecycledId;
                } else if (element.recycleId != element.lastRecycledId) {
                    throw new IllegalStateException("recycled already");
                }
                element.stack = to;
                trg[size++] = element;
                src[start++] = null;
            }
            ((Stack)to).size = size;
            if (end == 16 && head.next != null) {
                this.head = head.next;
            }
            head.readIndex = end;
            return true;
        }

        private static final class Link
        extends AtomicInteger {
            private final DefaultHandle[] elements = new DefaultHandle[16];
            private int readIndex;
            private Link next;

            private Link() {
            }
        }
    }

    static final class DefaultHandle
    implements Handle {
        private int lastRecycledId;
        private int recycleId;
        private Stack<?> stack;
        private Object value;

        DefaultHandle(Stack<?> stack) {
            this.stack = stack;
        }

        public void recycle() {
            Thread thread = Thread.currentThread();
            if (thread == this.stack.thread) {
                this.stack.push(this);
                return;
            }
            Map delayedRecycled = (Map)DELAYED_RECYCLED.get();
            WeakOrderQueue queue = (WeakOrderQueue)delayedRecycled.get(this.stack);
            if (queue == null) {
                queue = new WeakOrderQueue(this.stack, thread);
                delayedRecycled.put(this.stack, queue);
            }
            queue.add(this);
        }
    }

    public static interface Handle {
    }
}

