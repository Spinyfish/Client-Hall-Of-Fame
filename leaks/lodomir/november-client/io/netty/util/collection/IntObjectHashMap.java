/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.collection;

import io.netty.util.collection.IntObjectMap;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntObjectHashMap<V>
implements IntObjectMap<V>,
Iterable<IntObjectMap.Entry<V>> {
    private static final int DEFAULT_CAPACITY = 11;
    private static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private int[] keys;
    private V[] values;
    private int size;

    public IntObjectHashMap() {
        this(11, 0.5f);
    }

    public IntObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public IntObjectHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be >= 1");
        }
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = IntObjectHashMap.adjustCapacity(initialCapacity);
        this.keys = new int[capacity];
        Object[] temp = new Object[capacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(capacity);
    }

    private static <T> T toExternal(T value) {
        return value == NULL_VALUE ? null : (T)value;
    }

    private static <T> T toInternal(T value) {
        return (T)(value == null ? NULL_VALUE : value);
    }

    @Override
    public V get(int key) {
        int index = this.indexOf(key);
        return index == -1 ? null : (V)IntObjectHashMap.toExternal(this.values[index]);
    }

    @Override
    public V put(int key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = IntObjectHashMap.toInternal(value);
                this.growSize();
                return null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = IntObjectHashMap.toInternal(value);
            return IntObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext(index)) != startIndex);
        throw new IllegalStateException("Unable to insert");
    }

    private int probeNext(int index) {
        return index == this.values.length - 1 ? 0 : index + 1;
    }

    @Override
    public void putAll(IntObjectMap<V> sourceMap) {
        if (sourceMap instanceof IntObjectHashMap) {
            IntObjectHashMap source = (IntObjectHashMap)sourceMap;
            for (int i = 0; i < source.values.length; ++i) {
                V sourceValue = source.values[i];
                if (sourceValue == null) continue;
                this.put(source.keys[i], sourceValue);
            }
            return;
        }
        for (IntObjectMap.Entry<V> entry : sourceMap.entries()) {
            this.put(entry.key(), entry.value());
        }
    }

    @Override
    public V remove(int key) {
        int index = this.indexOf(key);
        if (index == -1) {
            return null;
        }
        V prev = this.values[index];
        this.removeAt(index);
        return IntObjectHashMap.toExternal(prev);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(this.keys, 0);
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(int key) {
        return this.indexOf(key) >= 0;
    }

    @Override
    public boolean containsValue(V value) {
        V v = IntObjectHashMap.toInternal(value);
        for (int i = 0; i < this.values.length; ++i) {
            if (this.values[i] == null || !this.values[i].equals(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterable<IntObjectMap.Entry<V>> entries() {
        return this;
    }

    @Override
    public Iterator<IntObjectMap.Entry<V>> iterator() {
        return new IteratorImpl();
    }

    @Override
    public int[] keys() {
        int[] outKeys = new int[this.size()];
        int targetIx = 0;
        for (int i = 0; i < this.values.length; ++i) {
            if (this.values[i] == null) continue;
            outKeys[targetIx++] = this.keys[i];
        }
        return outKeys;
    }

    @Override
    public V[] values(Class<V> clazz) {
        Object[] outValues = (Object[])Array.newInstance(clazz, this.size());
        int targetIx = 0;
        for (int i = 0; i < this.values.length; ++i) {
            if (this.values[i] == null) continue;
            outValues[targetIx++] = this.values[i];
        }
        return outValues;
    }

    public int hashCode() {
        int hash = this.size;
        for (int i = 0; i < this.keys.length; ++i) {
            hash ^= this.keys[i];
        }
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntObjectMap)) {
            return false;
        }
        IntObjectMap other = (IntObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            int key = this.keys[i];
            Object otherValue = other.get(key);
            if (!(value == NULL_VALUE ? otherValue != null : !value.equals(otherValue))) continue;
            return false;
        }
        return true;
    }

    private int indexOf(int key) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                return -1;
            }
            if (key != this.keys[index]) continue;
            return index;
        } while ((index = this.probeNext(index)) != startIndex);
        return -1;
    }

    private int hashIndex(int key) {
        return key % this.keys.length;
    }

    private void growSize() {
        ++this.size;
        if (this.size > this.maxSize) {
            this.rehash(IntObjectHashMap.adjustCapacity((int)Math.min((double)this.keys.length * 2.0, 2.147483639E9)));
        } else if (this.size == this.keys.length) {
            this.rehash(this.keys.length);
        }
    }

    private static int adjustCapacity(int capacity) {
        return capacity | 1;
    }

    private void removeAt(int index) {
        --this.size;
        this.keys[index] = 0;
        this.values[index] = null;
        int nextFree = index;
        int i = this.probeNext(index);
        while (this.values[i] != null) {
            int bucket = this.hashIndex(this.keys[i]);
            if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
                this.keys[nextFree] = this.keys[i];
                this.values[nextFree] = this.values[i];
                this.keys[i] = 0;
                this.values[i] = null;
                nextFree = i;
            }
            i = this.probeNext(i);
        }
    }

    private int calcMaxSize(int capacity) {
        int upperBound = capacity - 1;
        return Math.min(upperBound, (int)((float)capacity * this.loadFactor));
    }

    private void rehash(int newCapacity) {
        int[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new int[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(newCapacity);
        block0: for (int i = 0; i < oldVals.length; ++i) {
            int startIndex;
            V oldVal = oldVals[i];
            if (oldVal == null) continue;
            int oldKey = oldKeys[i];
            int index = startIndex = this.hashIndex(oldKey);
            while (true) {
                if (this.values[index] == null) {
                    this.keys[index] = oldKey;
                    this.values[index] = IntObjectHashMap.toInternal(oldVal);
                    continue block0;
                }
                index = this.probeNext(index);
            }
        }
    }

    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(4 * this.size);
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            sb.append(sb.length() == 0 ? "{" : ", ");
            sb.append(this.keys[i]).append('=').append((Object)(value == this ? "(this Map)" : value));
        }
        return sb.append('}').toString();
    }

    private final class IteratorImpl
    implements Iterator<IntObjectMap.Entry<V>>,
    IntObjectMap.Entry<V> {
        private int prevIndex = -1;
        private int nextIndex = -1;
        private int entryIndex = -1;

        private IteratorImpl() {
        }

        private void scanNext() {
            while (++this.nextIndex != IntObjectHashMap.this.values.length && IntObjectHashMap.this.values[this.nextIndex] == null) {
            }
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == -1) {
                this.scanNext();
            }
            return this.nextIndex < IntObjectHashMap.this.keys.length;
        }

        @Override
        public IntObjectMap.Entry<V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.prevIndex = this.nextIndex;
            this.scanNext();
            this.entryIndex = this.prevIndex;
            return this;
        }

        @Override
        public void remove() {
            if (this.prevIndex < 0) {
                throw new IllegalStateException("next must be called before each remove.");
            }
            IntObjectHashMap.this.removeAt(this.prevIndex);
            this.prevIndex = -1;
        }

        @Override
        public int key() {
            return IntObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V value() {
            return IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public void setValue(V value) {
            ((IntObjectHashMap)IntObjectHashMap.this).values[this.entryIndex] = IntObjectHashMap.toInternal(value);
        }
    }
}

