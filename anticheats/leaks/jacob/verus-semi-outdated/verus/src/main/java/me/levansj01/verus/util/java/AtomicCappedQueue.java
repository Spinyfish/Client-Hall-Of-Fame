package me.levansj01.verus.util.java;

import java.util.concurrent.atomic.*;
import java.util.*;
import org.jetbrains.annotations.*;

public class AtomicCappedQueue implements BasicDeque
{
    private final AtomicInteger startIndex;
    private final AtomicReferenceArray internal;
    private final int length;
    private final AtomicInteger lastIndex;
    
    static int access$300(final int n, final int n2) {
        return modulo(n, n2);
    }
    
    @NotNull
    @Override
    public Iterator iterator() {
        return new Iterator(this) {
            private int current = AtomicCappedQueue.access$000(this.this$0).get();
            final AtomicCappedQueue this$0;
            
            @Override
            public Object next() {
                return AtomicCappedQueue.access$400(this.this$0).get(AtomicCappedQueue.access$300(this.current++, AtomicCappedQueue.access$200(this.this$0)));
            }
            
            @Override
            public boolean hasNext() {
                return this.current < AtomicCappedQueue.access$100(this.this$0).get();
            }
        };
    }
    
    @Override
    public int size() {
        return this.lastIndex.get() - this.startIndex.get();
    }
    
    static AtomicInteger access$000(final AtomicCappedQueue atomicCappedQueue) {
        return atomicCappedQueue.startIndex;
    }
    
    @Override
    public Object peekLast() {
        return this.internal.get(modulo(this.lastIndex.get() - 1, this.length));
    }
    
    public AtomicCappedQueue(final int length) {
        this.lastIndex = new AtomicInteger(0);
        this.startIndex = new AtomicInteger(0);
        if (length <= 1) {
            throw new IllegalArgumentException("Cannot have length of " + length);
        }
        this.length = length;
        this.internal = new AtomicReferenceArray(length);
    }
    
    @Override
    public void addLast(final Object o) {
        if (this.internal.getAndSet(modulo(this.lastIndex.getAndIncrement(), this.length), o) != null) {
            this.startIndex.incrementAndGet();
        }
    }
    
    @Override
    public void addFirst(final Object o) {
        if (this.internal.getAndSet(modulo(this.startIndex.decrementAndGet(), this.length), o) != null) {
            this.lastIndex.decrementAndGet();
        }
    }
    
    @Override
    public Object pollLast() {
        return this.internal.getAndSet(modulo(this.lastIndex.decrementAndGet(), this.length), null);
    }
    
    @Override
    public void clear() {
        while (0 < this.length) {
            this.internal.set(0, null);
            int n = 0;
            ++n;
        }
        this.lastIndex.set(this.startIndex.get());
    }
    
    @Override
    public Object peekFirst() {
        return this.internal.get(modulo(this.startIndex.get(), this.length));
    }
    
    private static int modulo(final int n, final int n2) {
        int n3 = n % n2;
        if (n3 < 0) {
            n3 += n2;
        }
        return n3;
    }
    
    static int access$200(final AtomicCappedQueue atomicCappedQueue) {
        return atomicCappedQueue.length;
    }
    
    @Override
    public Object pollFirst() {
        return this.internal.getAndSet(modulo(this.startIndex.getAndIncrement(), this.length), null);
    }
    
    static AtomicInteger access$100(final AtomicCappedQueue atomicCappedQueue) {
        return atomicCappedQueue.lastIndex;
    }
    
    static AtomicReferenceArray access$400(final AtomicCappedQueue atomicCappedQueue) {
        return atomicCappedQueue.internal;
    }
}
