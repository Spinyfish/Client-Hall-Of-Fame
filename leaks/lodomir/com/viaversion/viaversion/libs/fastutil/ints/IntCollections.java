/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.libs.fastutil.ints.IntCollections$SynchronizedCollection
 *  com.viaversion.viaversion.libs.fastutil.ints.IntCollections$UnmodifiableCollection
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrays;
import com.viaversion.viaversion.libs.fastutil.ints.IntBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollections;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterable;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterators;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterators;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectArrays;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class IntCollections {
    private IntCollections() {
    }

    public static IntCollection synchronize(IntCollection c) {
        return new SynchronizedCollection(c);
    }

    public static IntCollection synchronize(IntCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static IntCollection unmodifiable(IntCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static IntCollection asCollection(IntIterable iterable) {
        if (iterable instanceof IntCollection) {
            return (IntCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    public static class IterableCollection
    extends AbstractIntCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntIterable iterable;

        protected IterableCollection(IntIterable iterable) {
            this.iterable = Objects.requireNonNull(iterable);
        }

        @Override
        public int size() {
            long size = this.iterable.spliterator().getExactSizeIfKnown();
            if (size >= 0L) {
                return (int)Math.min(Integer.MAX_VALUE, size);
            }
            int c = 0;
            IntIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextInt();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public IntIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        public IntSpliterator spliterator() {
            return this.iterable.spliterator();
        }

        @Override
        public IntIterator intIterator() {
            return this.iterable.intIterator();
        }

        @Override
        public IntSpliterator intSpliterator() {
            return this.iterable.intSpliterator();
        }
    }

    static class SizeDecreasingSupplier<C extends IntCollection>
    implements Supplier<C> {
        static final int RECOMMENDED_MIN_SIZE = 8;
        final AtomicInteger suppliedCount = new AtomicInteger(0);
        final int expectedFinalSize;
        final IntFunction<C> builder;

        SizeDecreasingSupplier(int expectedFinalSize, IntFunction<C> builder) {
            this.expectedFinalSize = expectedFinalSize;
            this.builder = builder;
        }

        @Override
        public C get() {
            int expectedNeededNextSize = 1 + (this.expectedFinalSize - 1) / this.suppliedCount.incrementAndGet();
            if (expectedNeededNextSize < 0) {
                expectedNeededNextSize = 8;
            }
            return (C)((IntCollection)this.builder.apply(expectedNeededNextSize));
        }
    }

    public static abstract class EmptyCollection
    extends AbstractIntCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(int k) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return ObjectArrays.EMPTY_ARRAY;
        }

        @Override
        public <T> T[] toArray(T[] array) {
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntSpliterator spliterator() {
            return IntSpliterators.EMPTY_SPLITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Collection)) {
                return false;
            }
            return ((Collection)o).isEmpty();
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Integer> action) {
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
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
        @Deprecated
        public boolean removeIf(Predicate<? super Integer> filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public int[] toIntArray() {
            return IntArrays.EMPTY_ARRAY;
        }

        @Override
        @Deprecated
        public int[] toIntArray(int[] a) {
            return a;
        }

        @Override
        public void forEach(IntConsumer action) {
        }

        @Override
        public boolean containsAll(IntCollection c) {
            return c.isEmpty();
        }

        @Override
        public boolean addAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(IntPredicate filter) {
            Objects.requireNonNull(filter);
            return false;
        }
    }
}

