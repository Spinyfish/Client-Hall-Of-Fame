/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class IteratorCache {
    private static Deque<IteratorReusable<Object>> dequeIterators = new ArrayDeque<IteratorReusable<Object>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Iterator<Object> getReadOnly(List list) {
        Deque<IteratorReusable<Object>> deque = dequeIterators;
        synchronized (deque) {
            IteratorReadOnly iteratorreusable = dequeIterators.pollFirst();
            if (iteratorreusable == null) {
                iteratorreusable = new IteratorReadOnly();
            }
            iteratorreusable.setList((List<Object>)list);
            return iteratorreusable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void finished(IteratorReusable<Object> iterator) {
        Deque<IteratorReusable<Object>> deque = dequeIterators;
        synchronized (deque) {
            if (dequeIterators.size() <= 1000) {
                iterator.setList(null);
                dequeIterators.addLast(iterator);
            }
        }
    }

    static {
        for (int i = 0; i < 1000; ++i) {
            IteratorReadOnly iteratorcache$iteratorreadonly = new IteratorReadOnly();
            dequeIterators.add(iteratorcache$iteratorreadonly);
        }
    }

    public static interface IteratorReusable<E>
    extends Iterator<E> {
        public void setList(List<E> var1);
    }

    public static class IteratorReadOnly
    implements IteratorReusable<Object> {
        private List<Object> list;
        private int index;
        private boolean hasNext;

        @Override
        public void setList(List<Object> list) {
            if (this.hasNext) {
                throw new RuntimeException("Iterator still used, oldList: " + this.list + ", newList: " + list);
            }
            this.list = list;
            this.index = 0;
            this.hasNext = list != null && this.index < list.size();
        }

        @Override
        public Object next() {
            if (!this.hasNext) {
                return null;
            }
            Object object = this.list.get(this.index);
            ++this.index;
            this.hasNext = this.index < this.list.size();
            return object;
        }

        @Override
        public boolean hasNext() {
            if (!this.hasNext) {
                IteratorCache.finished(this);
                return false;
            }
            return this.hasNext;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

