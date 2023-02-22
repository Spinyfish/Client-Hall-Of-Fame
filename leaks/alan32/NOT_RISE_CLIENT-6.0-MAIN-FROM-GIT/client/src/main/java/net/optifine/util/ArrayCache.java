package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class ArrayCache {
    private Class elementClass = null;
    private int maxCacheSize = 0;
    private final ArrayDeque cache = new ArrayDeque();

    public ArrayCache(final Class elementClass, final int maxCacheSize) {
        this.elementClass = elementClass;
        this.maxCacheSize = maxCacheSize;
    }

    public synchronized Object allocate(final int size) {
        Object object = this.cache.pollLast();

        if (object == null || Array.getLength(object) < size) {
            object = Array.newInstance(this.elementClass, size);
        }

        return object;
    }

    public synchronized void free(final Object arr) {
        if (arr != null) {
            final Class oclass = arr.getClass();

            if (oclass.getComponentType() != this.elementClass) {
                throw new IllegalArgumentException("Wrong component type");
            } else if (this.cache.size() < this.maxCacheSize) {
                this.cache.add(arr);
            }
        }
    }
}
