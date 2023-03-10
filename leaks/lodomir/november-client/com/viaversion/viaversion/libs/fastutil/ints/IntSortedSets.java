/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets$SynchronizedSortedSet
 *  com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets$UnmodifiableSortedSet
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.IntBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterators;
import com.viaversion.viaversion.libs.fastutil.ints.IntSets;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.NoSuchElementException;

public final class IntSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private IntSortedSets() {
    }

    public static IntSortedSet singleton(int element) {
        return new Singleton(element);
    }

    public static IntSortedSet singleton(int element, IntComparator comparator) {
        return new Singleton(element, comparator);
    }

    public static IntSortedSet singleton(Object element) {
        return new Singleton((Integer)element);
    }

    public static IntSortedSet singleton(Object element, IntComparator comparator) {
        return new Singleton((Integer)element, comparator);
    }

    public static IntSortedSet synchronize(IntSortedSet s) {
        return new SynchronizedSortedSet(s);
    }

    public static IntSortedSet synchronize(IntSortedSet s, Object sync) {
        return new SynchronizedSortedSet(s, sync);
    }

    public static IntSortedSet unmodifiable(IntSortedSet s) {
        return new UnmodifiableSortedSet(s);
    }

    public static class Singleton
    extends IntSets.Singleton
    implements IntSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final IntComparator comparator;

        protected Singleton(int element, IntComparator comparator) {
            super(element);
            this.comparator = comparator;
        }

        Singleton(int element) {
            this(element, null);
        }

        final int compare(int k1, int k2) {
            return this.comparator == null ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            IntBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.nextInt();
            }
            return i;
        }

        @Override
        public IntComparator comparator() {
            return this.comparator;
        }

        @Override
        public IntSpliterator spliterator() {
            return IntSpliterators.singleton(this.element, this.comparator);
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return EMPTY_SET;
        }

        @Override
        public IntSortedSet headSet(int to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return EMPTY_SET;
        }

        @Override
        public IntSortedSet tailSet(int from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return EMPTY_SET;
        }

        @Override
        public int firstInt() {
            return this.element;
        }

        @Override
        public int lastInt() {
            return this.element;
        }

        @Override
        @Deprecated
        public IntSortedSet subSet(Integer from, Integer to) {
            return this.subSet((int)from, (int)to);
        }

        @Override
        @Deprecated
        public IntSortedSet headSet(Integer to) {
            return this.headSet((int)to);
        }

        @Override
        @Deprecated
        public IntSortedSet tailSet(Integer from) {
            return this.tailSet((int)from);
        }

        @Override
        @Deprecated
        public Integer first() {
            return this.element;
        }

        @Override
        @Deprecated
        public Integer last() {
            return this.element;
        }
    }

    public static class EmptySet
    extends IntSets.EmptySet
    implements IntSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return EMPTY_SET;
        }

        @Override
        public IntSortedSet headSet(int from) {
            return EMPTY_SET;
        }

        @Override
        public IntSortedSet tailSet(int to) {
            return EMPTY_SET;
        }

        @Override
        public int firstInt() {
            throw new NoSuchElementException();
        }

        @Override
        public int lastInt() {
            throw new NoSuchElementException();
        }

        @Override
        public IntComparator comparator() {
            return null;
        }

        @Override
        @Deprecated
        public IntSortedSet subSet(Integer from, Integer to) {
            return EMPTY_SET;
        }

        @Override
        @Deprecated
        public IntSortedSet headSet(Integer from) {
            return EMPTY_SET;
        }

        @Override
        @Deprecated
        public IntSortedSet tailSet(Integer to) {
            return EMPTY_SET;
        }

        @Override
        @Deprecated
        public Integer first() {
            throw new NoSuchElementException();
        }

        @Override
        @Deprecated
        public Integer last() {
            throw new NoSuchElementException();
        }

        @Override
        public Object clone() {
            return EMPTY_SET;
        }

        private Object readResolve() {
            return EMPTY_SET;
        }
    }
}

