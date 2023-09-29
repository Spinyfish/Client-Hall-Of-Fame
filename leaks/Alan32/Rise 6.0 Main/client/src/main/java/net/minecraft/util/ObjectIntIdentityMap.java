package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T> {
    private final IdentityHashMap<T, Integer> identityMap = new IdentityHashMap(512);
    private final List<T> objectList = Lists.newArrayList();

    public void put(final T key, final int value) {
        this.identityMap.put(key, Integer.valueOf(value));

        while (this.objectList.size() <= value) {
            this.objectList.add(null);
        }

        this.objectList.set(value, key);
    }

    public int get(final T key) {
        final Integer integer = this.identityMap.get(key);
        return integer == null ? -1 : integer.intValue();
    }

    public final T getByValue(final int value) {
        return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
    }

    public Iterator<T> iterator() {
        return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
    }
}
