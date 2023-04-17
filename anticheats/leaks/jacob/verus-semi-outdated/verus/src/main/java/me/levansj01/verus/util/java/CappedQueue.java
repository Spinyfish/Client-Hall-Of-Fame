package me.levansj01.verus.util.java;

import java.util.*;
import org.jetbrains.annotations.*;

public class CappedQueue implements BasicDeque
{
    private int lastIndex;
    private int startIndex;
    private final Object[] internal;
    
    public CappedQueue(final int n) {
        if (n <= 1) {
            throw new IllegalArgumentException("Cannot have length of " + n);
        }
        this.internal = new Object[n];
    }
    
    @Override
    public Object peekFirst() {
        return this.internal[modulo(this.startIndex, this.internal.length)];
    }
    
    static Object[] access$200(final CappedQueue cappedQueue) {
        return cappedQueue.internal;
    }
    
    @Override
    public Object pollFirst() {
        return this.getAndSetInternal(modulo(this.startIndex++, this.internal.length), null);
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.internal, null);
        this.lastIndex = this.startIndex;
    }
    
    @Override
    public Object pollLast() {
        final int lastIndex = this.lastIndex - 1;
        this.lastIndex = lastIndex;
        return this.getAndSetInternal(modulo(lastIndex, this.internal.length), null);
    }
    
    @NotNull
    @Override
    public Iterator iterator() {
        return new Iterator(this) {
            private int current = CappedQueue.access$000(this.this$0);
            final CappedQueue this$0;
            
            @Override
            public Object next() {
                return CappedQueue.access$200(this.this$0)[CappedQueue.access$300(this.current++, CappedQueue.access$200(this.this$0).length)];
            }
            
            @Override
            public boolean hasNext() {
                return this.current < CappedQueue.access$100(this.this$0);
            }
        };
    }
    
    @Override
    public void addLast(final Object o) {
        if (this.getAndSetInternal(modulo(this.lastIndex++, this.internal.length), o) != null) {
            ++this.startIndex;
        }
    }
    
    static int access$000(final CappedQueue cappedQueue) {
        return cappedQueue.startIndex;
    }
    
    private Object getAndSetInternal(final int n, final Object o) {
        final Object o2 = this.internal[n];
        this.internal[n] = o;
        return o2;
    }
    
    @Override
    public int size() {
        return this.lastIndex - this.startIndex;
    }
    
    private static int modulo(final int n, final int n2) {
        int n3 = n % n2;
        if (n3 < 0) {
            n3 += n2;
        }
        return n3;
    }
    
    static int access$300(final int n, final int n2) {
        return modulo(n, n2);
    }
    
    @Override
    public void addFirst(final Object o) {
        final int startIndex = this.startIndex - 1;
        this.startIndex = startIndex;
        if (this.getAndSetInternal(modulo(startIndex, this.internal.length), o) != null) {
            --this.lastIndex;
        }
    }
    
    @Override
    public Object peekLast() {
        return this.internal[modulo(this.lastIndex - 1, this.internal.length)];
    }
    
    static int access$100(final CappedQueue cappedQueue) {
        return cappedQueue.lastIndex;
    }
}
