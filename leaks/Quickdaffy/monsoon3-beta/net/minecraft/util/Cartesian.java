/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.UnmodifiableIterator
 */
package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cartesian {
    public static <T> Iterable<T[]> cartesianProduct(Class<T> clazz, Iterable<? extends Iterable<? extends T>> sets) {
        return new Product(clazz, Cartesian.toArray(Iterable.class, sets));
    }

    public static <T> Iterable<List<T>> cartesianProduct(Iterable<? extends Iterable<? extends T>> sets) {
        return Cartesian.arraysAsLists(Cartesian.cartesianProduct(Object.class, sets));
    }

    private static <T> Iterable<List<T>> arraysAsLists(Iterable<Object[]> arrays) {
        return Iterables.transform(arrays, new GetList());
    }

    private static <T> T[] toArray(Class<? super T> clazz, Iterable<? extends T> it) {
        ArrayList list = Lists.newArrayList();
        for (T t : it) {
            list.add(t);
        }
        return list.toArray(Cartesian.createArray(clazz, list.size()));
    }

    private static <T> T[] createArray(Class<? super T> p_179319_0_, int p_179319_1_) {
        return (Object[])Array.newInstance(p_179319_0_, p_179319_1_);
    }

    static class Product<T>
    implements Iterable<T[]> {
        private final Class<T> clazz;
        private final Iterable<? extends T>[] iterables;

        private Product(Class<T> clazz, Iterable<? extends T>[] iterables) {
            this.clazz = clazz;
            this.iterables = iterables;
        }

        @Override
        public Iterator<T[]> iterator() {
            return this.iterables.length <= 0 ? Collections.singletonList(Cartesian.createArray(this.clazz, 0)).iterator() : new ProductIterator(this.clazz, this.iterables);
        }

        static class ProductIterator<T>
        extends UnmodifiableIterator<T[]> {
            private int index = -2;
            private final Iterable<? extends T>[] iterables;
            private final Iterator<? extends T>[] iterators;
            private final T[] results;

            private ProductIterator(Class<T> clazz, Iterable<? extends T>[] iterables) {
                this.iterables = iterables;
                this.iterators = (Iterator[])Cartesian.createArray(Iterator.class, this.iterables.length);
                for (int i = 0; i < this.iterables.length; ++i) {
                    this.iterators[i] = iterables[i].iterator();
                }
                this.results = Cartesian.createArray(clazz, this.iterators.length);
            }

            private void endOfData() {
                this.index = -1;
                Arrays.fill(this.iterators, null);
                Arrays.fill(this.results, null);
            }

            public boolean hasNext() {
                if (this.index == -2) {
                    this.index = 0;
                    for (Iterator<T> iterator : this.iterators) {
                        if (iterator.hasNext()) continue;
                        this.endOfData();
                        break;
                    }
                    return true;
                }
                if (this.index >= this.iterators.length) {
                    Iterator<T> iterator;
                    this.index = this.iterators.length - 1;
                    while (this.index >= 0 && !(iterator = this.iterators[this.index]).hasNext()) {
                        if (this.index == 0) {
                            this.endOfData();
                            break;
                        }
                        iterator = this.iterables[this.index].iterator();
                        this.iterators[this.index] = iterator;
                        if (!iterator.hasNext()) {
                            this.endOfData();
                            break;
                        }
                        --this.index;
                    }
                }
                return this.index >= 0;
            }

            public T[] next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                while (this.index < this.iterators.length) {
                    this.results[this.index] = this.iterators[this.index].next();
                    ++this.index;
                }
                return (Object[])this.results.clone();
            }
        }
    }

    static class GetList<T>
    implements Function<Object[], List<T>> {
        private GetList() {
        }

        public List<T> apply(Object[] p_apply_1_) {
            return Arrays.asList(p_apply_1_);
        }
    }
}

