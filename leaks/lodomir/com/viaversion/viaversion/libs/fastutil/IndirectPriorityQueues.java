/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

import com.viaversion.viaversion.libs.fastutil.IndirectPriorityQueue;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class IndirectPriorityQueues {
    public static final EmptyIndirectPriorityQueue EMPTY_QUEUE = new EmptyIndirectPriorityQueue();

    private IndirectPriorityQueues() {
    }

    public static <K> IndirectPriorityQueue<K> synchronize(IndirectPriorityQueue<K> q) {
        return new SynchronizedIndirectPriorityQueue<K>(q);
    }

    public static <K> IndirectPriorityQueue<K> synchronize(IndirectPriorityQueue<K> q, Object sync) {
        return new SynchronizedIndirectPriorityQueue<K>(q, sync);
    }

    public static class SynchronizedIndirectPriorityQueue<K>
    implements IndirectPriorityQueue<K> {
        public static final long serialVersionUID = -7046029254386353129L;
        protected final IndirectPriorityQueue<K> q;
        protected final Object sync;

        protected SynchronizedIndirectPriorityQueue(IndirectPriorityQueue<K> q, Object sync) {
            this.q = q;
            this.sync = sync;
        }

        protected SynchronizedIndirectPriorityQueue(IndirectPriorityQueue<K> q) {
            this.q = q;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(int x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int dequeue() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(int index) {
            Object object = this.sync;
            synchronized (object) {
                return this.q.contains(index);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int first() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int last() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.last();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.q.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void changed() {
            Object object = this.sync;
            synchronized (object) {
                this.q.changed();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void allChanged() {
            Object object = this.sync;
            synchronized (object) {
                this.q.allChanged();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void changed(int i) {
            Object object = this.sync;
            synchronized (object) {
                this.q.changed(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.q.remove(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Comparator<? super K> comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.comparator();
            }
        }

        @Override
        public int front(int[] a) {
            return this.q.front(a);
        }
    }

    public static class EmptyIndirectPriorityQueue
    implements IndirectPriorityQueue {
        protected EmptyIndirectPriorityQueue() {
        }

        @Override
        public void enqueue(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int dequeue() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(int index) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public int first() {
            throw new NoSuchElementException();
        }

        @Override
        public int last() {
            throw new NoSuchElementException();
        }

        @Override
        public void changed() {
            throw new NoSuchElementException();
        }

        @Override
        public void allChanged() {
        }

        public Comparator<?> comparator() {
            return null;
        }

        @Override
        public void changed(int i) {
            throw new IllegalArgumentException("Index " + i + " is not in the queue");
        }

        @Override
        public boolean remove(int i) {
            return false;
        }

        @Override
        public int front(int[] a) {
            return 0;
        }
    }
}

