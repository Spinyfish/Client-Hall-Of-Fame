/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.MpscLinkedQueueNode;
import io.netty.util.internal.MpscLinkedQueueTailRef;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

final class MpscLinkedQueue<E>
extends MpscLinkedQueueTailRef<E>
implements Queue<E> {
    private static final long serialVersionUID = -1878402552271506449L;
    long p00;
    long p01;
    long p02;
    long p03;
    long p04;
    long p05;
    long p06;
    long p07;
    long p30;
    long p31;
    long p32;
    long p33;
    long p34;
    long p35;
    long p36;
    long p37;

    MpscLinkedQueue() {
        DefaultNode<Object> tombstone = new DefaultNode<Object>(null);
        this.setHeadRef(tombstone);
        this.setTailRef(tombstone);
    }

    private MpscLinkedQueueNode<E> peekNode() {
        MpscLinkedQueueNode head;
        do {
            MpscLinkedQueueNode next;
            if ((next = (head = this.headRef()).next()) == null) continue;
            return next;
        } while (head != this.tailRef());
        return null;
    }

    @Override
    public boolean offer(E value) {
        DefaultNode<E> newTail;
        if (value == null) {
            throw new NullPointerException("value");
        }
        if (value instanceof MpscLinkedQueueNode) {
            newTail = (DefaultNode<E>)value;
            newTail.setNext(null);
        } else {
            newTail = new DefaultNode<E>(value);
        }
        MpscLinkedQueueNode<E> oldTail = this.getAndSetTailRef(newTail);
        oldTail.setNext(newTail);
        return true;
    }

    @Override
    public E poll() {
        MpscLinkedQueueNode<E> next = this.peekNode();
        if (next == null) {
            return null;
        }
        MpscLinkedQueueNode oldHead = this.headRef();
        this.lazySetHeadRef(next);
        oldHead.unlink();
        return next.clearMaybe();
    }

    @Override
    public E peek() {
        MpscLinkedQueueNode<E> next = this.peekNode();
        if (next == null) {
            return null;
        }
        return next.value();
    }

    @Override
    public int size() {
        int count = 0;
        for (MpscLinkedQueueNode<E> n = this.peekNode(); n != null; n = n.next()) {
            ++count;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return this.peekNode() == null;
    }

    @Override
    public boolean contains(Object o) {
        for (MpscLinkedQueueNode<E> n = this.peekNode(); n != null; n = n.next()) {
            if (n.value() != o) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>(){
            private MpscLinkedQueueNode<E> node;
            {
                this.node = MpscLinkedQueue.this.peekNode();
            }

            @Override
            public boolean hasNext() {
                return this.node != null;
            }

            @Override
            public E next() {
                MpscLinkedQueueNode node = this.node;
                if (node == null) {
                    throw new NoSuchElementException();
                }
                Object value = node.value();
                this.node = node.next();
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(E e) {
        if (this.offer(e)) {
            return true;
        }
        throw new IllegalStateException("queue full");
    }

    @Override
    public E remove() {
        E e = this.poll();
        if (e != null) {
            return e;
        }
        throw new NoSuchElementException();
    }

    @Override
    public E element() {
        E e = this.peek();
        if (e != null) {
            return e;
        }
        throw new NoSuchElementException();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[this.size()];
        Iterator<E> it = this.iterator();
        for (int i = 0; i < array.length; ++i) {
            if (!it.hasNext()) {
                return Arrays.copyOf(array, i);
            }
            array[i] = it.next();
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = this.size();
        Object[] array = a.length >= size ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        Iterator<E> it = this.iterator();
        for (int i = 0; i < array.length; ++i) {
            if (!it.hasNext()) {
                if (a == array) {
                    array[i] = null;
                    return array;
                }
                if (a.length < i) {
                    return Arrays.copyOf(array, i);
                }
                System.arraycopy(array, 0, a, 0, i);
                if (a.length > i) {
                    a[i] = null;
                }
                return a;
            }
            array[i] = it.next();
        }
        return array;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (this.contains(e)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("c");
        }
        if (c == this) {
            throw new IllegalArgumentException("c == this");
        }
        boolean modified = false;
        for (E e : c) {
            this.add(e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        while (this.poll() != null) {
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        for (E e : this) {
            out.writeObject(e);
        }
        out.writeObject(null);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object e;
        in.defaultReadObject();
        DefaultNode<Object> tombstone = new DefaultNode<Object>(null);
        this.setHeadRef(tombstone);
        this.setTailRef(tombstone);
        while ((e = in.readObject()) != null) {
            this.add(e);
        }
    }

    private static final class DefaultNode<T>
    extends MpscLinkedQueueNode<T> {
        private T value;

        DefaultNode(T value) {
            this.value = value;
        }

        @Override
        public T value() {
            return this.value;
        }

        @Override
        protected T clearMaybe() {
            T value = this.value;
            this.value = null;
            return value;
        }
    }
}

