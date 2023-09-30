package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.optifine.util.IteratorCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
    private static final Set<Class<?>> field_181158_a = Collections.<Class<?>>newSetFromMap(new ConcurrentHashMap());
    private final Map<Class<?>, List<T>> map = Maps.newHashMap();
    private final Set<Class<?>> knownKeys = Sets.newIdentityHashSet();
    private final Class<T> baseClass;
    private final List<T> field_181745_e = Lists.newArrayList();
    public boolean empty;

    public ClassInheritanceMultiMap(final Class<T> baseClassIn) {
        this.baseClass = baseClassIn;
        this.knownKeys.add(baseClassIn);
        this.map.put(baseClassIn, this.field_181745_e);

        for (final Class<?> oclass : field_181158_a) {
            this.createLookup(oclass);
        }

        this.empty = this.field_181745_e.size() == 0;
    }

    protected void createLookup(final Class<?> clazz) {
        field_181158_a.add(clazz);
        final int i = this.field_181745_e.size();

        for (int j = 0; j < i; ++j) {
            final T t = this.field_181745_e.get(j);

            if (clazz.isAssignableFrom(t.getClass())) {
                this.func_181743_a(t, clazz);
            }
        }

        this.knownKeys.add(clazz);
    }

    protected Class<?> func_181157_b(final Class<?> p_181157_1_) {
        if (this.baseClass.isAssignableFrom(p_181157_1_)) {
            if (!this.knownKeys.contains(p_181157_1_)) {
                this.createLookup(p_181157_1_);
            }

            return p_181157_1_;
        } else {
            throw new IllegalArgumentException("Don't know how to search for " + p_181157_1_);
        }
    }

    public boolean add(final T p_add_1_) {
        for (final Class<?> oclass : this.knownKeys) {
            if (oclass.isAssignableFrom(p_add_1_.getClass())) {
                this.func_181743_a(p_add_1_, oclass);
            }
        }

        this.empty = this.field_181745_e.size() == 0;
        return true;
    }

    private void func_181743_a(final T p_181743_1_, final Class<?> p_181743_2_) {
        final List<T> list = this.map.get(p_181743_2_);

        if (list == null) {
            this.map.put(p_181743_2_, Lists.newArrayList(p_181743_1_));
        } else {
            list.add(p_181743_1_);
        }

        this.empty = this.field_181745_e.size() == 0;
    }

    public boolean remove(final Object p_remove_1_) {
        final T t = (T) p_remove_1_;
        boolean flag = false;

        for (final Class<?> oclass : this.knownKeys) {
            if (oclass.isAssignableFrom(t.getClass())) {
                final List<T> list = this.map.get(oclass);

                if (list != null && list.remove(t)) {
                    flag = true;
                }
            }
        }

        this.empty = this.field_181745_e.size() == 0;
        return flag;
    }

    public boolean contains(final Object p_contains_1_) {
        return Iterators.contains(this.getByClass(p_contains_1_.getClass()).iterator(), p_contains_1_);
    }

    public <S> Iterable<S> getByClass(final Class<S> clazz) {
        return new Iterable<S>() {
            public Iterator<S> iterator() {
                final List<T> list = ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.func_181157_b(clazz));

                if (list == null) {
                    return Iterators.emptyIterator();
                } else {
                    final Iterator<T> iterator = list.iterator();
                    return Iterators.filter(iterator, clazz);
                }
            }
        };
    }

    public Iterator<T> iterator() {
        return (Iterator<T>) (this.field_181745_e.isEmpty() ? Iterators.emptyIterator() : IteratorCache.getReadOnly(this.field_181745_e));
    }

    public int size() {
        return this.field_181745_e.size();
    }

    public boolean isEmpty() {
        return this.empty;
    }
}
