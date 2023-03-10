/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.libs.flare.fastutil;

import com.viaversion.viaversion.libs.fastutil.ints.AbstractInt2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMaps;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.flare.fastutil.Int2ObjectSyncMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class Int2ObjectSyncMapImpl<V>
extends AbstractInt2ObjectMap<V>
implements Int2ObjectSyncMap<V> {
    private static final long serialVersionUID = 1L;
    private final transient Object lock = new Object();
    private volatile transient Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>> read;
    private volatile transient boolean amended;
    private transient Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>> dirty;
    private transient int misses;
    private final transient IntFunction<Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>>> function;
    private transient EntrySetView entrySet;

    Int2ObjectSyncMapImpl(@NonNull IntFunction<Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>>> function, int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        this.function = function;
        this.read = function.apply(initialCapacity);
    }

    @Override
    public int size() {
        this.promote();
        int size = 0;
        for (Int2ObjectSyncMap.ExpungingEntry value : this.read.values()) {
            if (!value.exists()) continue;
            ++size;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        this.promote();
        for (Int2ObjectSyncMap.ExpungingEntry value : this.read.values()) {
            if (!value.exists()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        for (Int2ObjectMap.Entry entry : this.int2ObjectEntrySet()) {
            if (!Objects.equals(entry.getValue(), value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(int key) {
        Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
        return entry != null && entry.exists();
    }

    @Override
    public @Nullable V get(int key) {
        Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
        return entry != null ? (V)entry.get() : null;
    }

    @Override
    public @NonNull V getOrDefault(int key, @NonNull V defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue");
        Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
        return entry != null ? entry.getOr(defaultValue) : defaultValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public @Nullable Int2ObjectSyncMap.ExpungingEntry<V> getEntry(int key) {
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        if (entry == null && this.amended) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry == null && this.amended && this.dirty != null) {
                    entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key);
                    this.missLocked();
                }
            }
        }
        return entry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V computeIfAbsent(int key, @NonNull IntFunction<? extends V> mappingFunction) {
        Int2ObjectSyncMap.InsertionResult<Object> result;
        Objects.requireNonNull(mappingFunction, "mappingFunction");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        Int2ObjectSyncMap.InsertionResult<V> insertionResult = result = entry != null ? entry.computeIfAbsent(key, mappingFunction) : null;
        if (result == null || result.operation() == 2) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry != null) {
                    if (entry.tryUnexpungeAndCompute(key, mappingFunction)) {
                        if (entry.exists()) {
                            this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<Int2ObjectSyncMap.ExpungingEntry>)entry);
                        }
                        return entry.get();
                    }
                    result = entry.computeIfAbsent(key, mappingFunction);
                } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
                    result = entry.computeIfAbsent(key, mappingFunction);
                    if (result.current() == null) {
                        this.dirty.remove(key);
                    }
                    this.missLocked();
                } else {
                    V computed;
                    if (!this.amended) {
                        this.dirtyLocked();
                        this.amended = true;
                    }
                    if ((computed = mappingFunction.apply(key)) != null) {
                        this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<ExpungingEntryImpl<V>>)new ExpungingEntryImpl<V>(computed));
                    }
                    return computed;
                }
            }
        }
        return result.current();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V computeIfAbsent(int key, @NonNull Int2ObjectFunction<? extends V> mappingFunction) {
        Int2ObjectSyncMap.InsertionResult<Object> result;
        Objects.requireNonNull(mappingFunction, "mappingFunction");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        Int2ObjectSyncMap.InsertionResult<V> insertionResult = result = entry != null ? entry.computeIfAbsentPrimitive(key, mappingFunction) : null;
        if (result == null || result.operation() == 2) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry != null) {
                    if (entry.tryUnexpungeAndComputePrimitive(key, mappingFunction)) {
                        if (entry.exists()) {
                            this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<Int2ObjectSyncMap.ExpungingEntry>)entry);
                        }
                        return entry.get();
                    }
                    result = entry.computeIfAbsentPrimitive(key, mappingFunction);
                } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
                    result = entry.computeIfAbsentPrimitive(key, mappingFunction);
                    if (result.current() == null) {
                        this.dirty.remove(key);
                    }
                    this.missLocked();
                } else {
                    V computed;
                    if (!this.amended) {
                        this.dirtyLocked();
                        this.amended = true;
                    }
                    if ((computed = mappingFunction.get(key)) != null) {
                        this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<ExpungingEntryImpl<V>>)new ExpungingEntryImpl<V>(computed));
                    }
                    return computed;
                }
            }
        }
        return result != null ? (V)result.current() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V computeIfPresent(int key, @NonNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        Int2ObjectSyncMap.InsertionResult<Object> result;
        Objects.requireNonNull(remappingFunction, "remappingFunction");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        Int2ObjectSyncMap.InsertionResult<V> insertionResult = result = entry != null ? entry.computeIfPresent(key, remappingFunction) : null;
        if (result == null || result.operation() == 2) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry != null) {
                    result = entry.computeIfPresent(key, remappingFunction);
                } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
                    result = entry.computeIfPresent(key, remappingFunction);
                    if (result.current() == null) {
                        this.dirty.remove(key);
                    }
                    this.missLocked();
                }
            }
        }
        return result != null ? (V)result.current() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V compute(int key, @NonNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        Int2ObjectSyncMap.InsertionResult<Object> result;
        Objects.requireNonNull(remappingFunction, "remappingFunction");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        Int2ObjectSyncMap.InsertionResult<V> insertionResult = result = entry != null ? entry.compute(key, remappingFunction) : null;
        if (result == null || result.operation() == 2) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry != null) {
                    if (entry.tryUnexpungeAndCompute(key, remappingFunction)) {
                        if (entry.exists()) {
                            this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<Int2ObjectSyncMap.ExpungingEntry>)entry);
                        }
                        return entry.get();
                    }
                    result = entry.compute(key, remappingFunction);
                } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
                    result = entry.compute(key, remappingFunction);
                    if (result.current() == null) {
                        this.dirty.remove(key);
                    }
                    this.missLocked();
                } else {
                    V computed;
                    if (!this.amended) {
                        this.dirtyLocked();
                        this.amended = true;
                    }
                    if ((computed = remappingFunction.apply(key, null)) != null) {
                        this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<ExpungingEntryImpl<V>>)new ExpungingEntryImpl<V>(computed));
                    }
                    return computed;
                }
            }
        }
        return result.current();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V putIfAbsent(int key, @NonNull V value) {
        Int2ObjectSyncMap.InsertionResult<V> result;
        Objects.requireNonNull(value, "value");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        Int2ObjectSyncMap.InsertionResult<V> insertionResult = result = entry != null ? entry.setIfAbsent(value) : null;
        if (result == null || result.operation() == 2) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry != null) {
                    if (entry.tryUnexpungeAndSet(value)) {
                        this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<Int2ObjectSyncMap.ExpungingEntry>)entry);
                    } else {
                        result = entry.setIfAbsent(value);
                    }
                } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
                    result = entry.setIfAbsent(value);
                    this.missLocked();
                } else {
                    if (!this.amended) {
                        this.dirtyLocked();
                        this.amended = true;
                    }
                    this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<ExpungingEntryImpl<V>>)new ExpungingEntryImpl<V>(value));
                }
            }
        }
        return result != null ? (V)result.previous() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V put(int key, @NonNull V value) {
        V previous;
        Objects.requireNonNull(value, "value");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        V v = previous = entry != null ? (V)entry.get() : null;
        if (entry != null && entry.trySet(value)) {
            return previous;
        }
        Object object = this.lock;
        synchronized (object) {
            entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
            if (entry != null) {
                previous = entry.get();
                if (entry.tryUnexpungeAndSet(value)) {
                    this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<Int2ObjectSyncMap.ExpungingEntry>)entry);
                } else {
                    entry.set(value);
                }
            } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
                previous = entry.get();
                entry.set(value);
                this.missLocked();
            } else {
                if (!this.amended) {
                    this.dirtyLocked();
                    this.amended = true;
                }
                this.dirty.put(key, (Int2ObjectSyncMap.ExpungingEntry<ExpungingEntryImpl<V>>)new ExpungingEntryImpl<V>(value));
            }
        }
        return previous;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable V remove(int key) {
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        if (entry == null && this.amended) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry == null && this.amended && this.dirty != null) {
                    entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.remove(key);
                    this.missLocked();
                }
            }
        }
        return entry != null ? (V)entry.clear() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(int key, @NonNull Object value) {
        Objects.requireNonNull(value, "value");
        Int2ObjectSyncMap.ExpungingEntry entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
        if (entry == null && this.amended) {
            Object object = this.lock;
            synchronized (object) {
                entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
                if (entry == null && this.amended && this.dirty != null) {
                    boolean present;
                    entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key);
                    boolean bl = present = entry != null && entry.replace(value, null);
                    if (present) {
                        this.dirty.remove(key);
                    }
                    this.missLocked();
                    return present;
                }
            }
        }
        return entry != null && entry.replace(value, null);
    }

    @Override
    public @Nullable V replace(int key, @NonNull V value) {
        Objects.requireNonNull(value, "value");
        Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
        return entry != null ? (V)entry.tryReplace(value) : null;
    }

    @Override
    public boolean replace(int key, @NonNull V oldValue, @NonNull V newValue) {
        Objects.requireNonNull(oldValue, "oldValue");
        Objects.requireNonNull(newValue, "newValue");
        Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
        return entry != null && entry.replace(oldValue, newValue);
    }

    @Override
    public void forEach(@NonNull BiConsumer<? super Integer, ? super V> action) {
        Objects.requireNonNull(action, "action");
        this.promote();
        for (Int2ObjectMap.Entry entry : this.read.int2ObjectEntrySet()) {
            Object value = ((Int2ObjectSyncMap.ExpungingEntry)entry.getValue()).get();
            if (value == null) continue;
            action.accept(entry.getIntKey(), value);
        }
    }

    @Override
    public void putAll(@NonNull Map<? extends Integer, ? extends V> map) {
        Objects.requireNonNull(map, "map");
        for (Map.Entry<Integer, V> entry : map.entrySet()) {
            this.put((int)entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void replaceAll(@NonNull BiFunction<? super Integer, ? super V, ? extends V> function) {
        Objects.requireNonNull(function, "function");
        this.promote();
        for (Int2ObjectMap.Entry entry : this.read.int2ObjectEntrySet()) {
            Int2ObjectSyncMap.ExpungingEntry entry2 = (Int2ObjectSyncMap.ExpungingEntry)entry.getValue();
            Object value = entry2.get();
            if (value == null) continue;
            entry2.tryReplace(function.apply(entry.getIntKey(), value));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Object object = this.lock;
        synchronized (object) {
            this.read = this.function.apply(this.read.size());
            this.dirty = null;
            this.amended = false;
            this.misses = 0;
        }
    }

    @Override
    public @NonNull ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
        if (this.entrySet != null) {
            return this.entrySet;
        }
        this.entrySet = new EntrySetView();
        return this.entrySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void promote() {
        if (this.amended) {
            Object object = this.lock;
            synchronized (object) {
                if (this.amended) {
                    this.promoteLocked();
                }
            }
        }
    }

    private void promoteLocked() {
        if (this.dirty != null) {
            this.read = this.dirty;
        }
        this.amended = false;
        this.dirty = null;
        this.misses = 0;
    }

    private void missLocked() {
        if (++this.misses >= this.dirty.size()) {
            this.promoteLocked();
        }
    }

    private void dirtyLocked() {
        if (this.dirty != null) {
            return;
        }
        this.dirty = this.function.apply(this.read.size());
        Int2ObjectMaps.fastForEach(this.read, entry -> {
            if (!((Int2ObjectSyncMap.ExpungingEntry)entry.getValue()).tryExpunge()) {
                this.dirty.put(entry.getIntKey(), (Int2ObjectSyncMap.ExpungingEntry<Int2ObjectSyncMap.ExpungingEntry>)((Int2ObjectSyncMap.ExpungingEntry)entry.getValue()));
            }
        });
    }

    final class EntryIterator
    implements ObjectIterator<Int2ObjectMap.Entry<V>> {
        private final Iterator<Int2ObjectMap.Entry<Int2ObjectSyncMap.ExpungingEntry<V>>> backingIterator;
        private Int2ObjectMap.Entry<V> next;
        private Int2ObjectMap.Entry<V> current;

        EntryIterator(Iterator<Int2ObjectMap.Entry<Int2ObjectSyncMap.ExpungingEntry<V>>> backingIterator) {
            this.backingIterator = backingIterator;
            this.next = this.nextValue();
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public @NonNull Int2ObjectMap.Entry<V> next() {
            this.current = this.next;
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            this.next = this.nextValue();
            return this.current;
        }

        private @Nullable Int2ObjectMap.Entry<V> nextValue() {
            while (this.backingIterator.hasNext()) {
                Int2ObjectMap.Entry entry = this.backingIterator.next();
                Object value = ((Int2ObjectSyncMap.ExpungingEntry)entry.getValue()).get();
                if (value == null) continue;
                return new MapEntry(entry.getIntKey(), value);
            }
            return null;
        }

        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            Int2ObjectSyncMapImpl.this.remove(this.current.getIntKey());
            this.current = null;
        }

        @Override
        public void forEachRemaining(@NonNull Consumer<? super Int2ObjectMap.Entry<V>> action) {
            Objects.requireNonNull(action, "action");
            if (this.next != null) {
                action.accept(this.next);
            }
            this.backingIterator.forEachRemaining((? super E entry) -> {
                Object value = ((Int2ObjectSyncMap.ExpungingEntry)entry.getValue()).get();
                if (value != null) {
                    action.accept(new MapEntry(entry.getIntKey(), value));
                }
            });
        }
    }

    final class EntrySetView
    extends AbstractObjectSet<Int2ObjectMap.Entry<V>> {
        EntrySetView() {
        }

        @Override
        public int size() {
            return Int2ObjectSyncMapImpl.this.size();
        }

        @Override
        public boolean contains(@Nullable Object entry) {
            if (!(entry instanceof Int2ObjectMap.Entry)) {
                return false;
            }
            Int2ObjectMap.Entry mapEntry = (Int2ObjectMap.Entry)entry;
            Object value = Int2ObjectSyncMapImpl.this.get(mapEntry.getIntKey());
            return value != null && Objects.equals(value, mapEntry.getValue());
        }

        @Override
        public boolean remove(@Nullable Object entry) {
            if (!(entry instanceof Int2ObjectMap.Entry)) {
                return false;
            }
            Int2ObjectMap.Entry mapEntry = (Int2ObjectMap.Entry)entry;
            return Int2ObjectSyncMapImpl.this.remove(mapEntry.getIntKey()) != null;
        }

        @Override
        public void clear() {
            Int2ObjectSyncMapImpl.this.clear();
        }

        @Override
        public @NonNull ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
            Int2ObjectSyncMapImpl.this.promote();
            return new EntryIterator(Int2ObjectSyncMapImpl.this.read.int2ObjectEntrySet().iterator());
        }
    }

    final class MapEntry
    implements Int2ObjectMap.Entry<V> {
        private final int key;
        private V value;

        MapEntry(@NonNull int key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int getIntKey() {
            return this.key;
        }

        @Override
        public @NonNull V getValue() {
            return this.value;
        }

        @Override
        public @Nullable V setValue(@NonNull V value) {
            Objects.requireNonNull(value, "value");
            this.value = value;
            return Int2ObjectSyncMapImpl.this.put(this.key, this.value);
        }

        public @NonNull String toString() {
            return "Int2ObjectSyncMapImpl.MapEntry{key=" + this.getIntKey() + ", value=" + this.getValue() + "}";
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Int2ObjectMap.Entry)) {
                return false;
            }
            Int2ObjectMap.Entry that = (Int2ObjectMap.Entry)other;
            return Objects.equals(this.getIntKey(), that.getIntKey()) && Objects.equals(this.getValue(), that.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getIntKey(), this.getValue());
        }
    }

    static final class InsertionResultImpl<V>
    implements Int2ObjectSyncMap.InsertionResult<V> {
        private static final byte UNCHANGED = 0;
        private static final byte UPDATED = 1;
        private static final byte EXPUNGED = 2;
        private final byte operation;
        private final V previous;
        private final V current;

        InsertionResultImpl(byte operation, @Nullable V previous, @Nullable V current) {
            this.operation = operation;
            this.previous = previous;
            this.current = current;
        }

        @Override
        public byte operation() {
            return this.operation;
        }

        @Override
        public @Nullable V previous() {
            return this.previous;
        }

        @Override
        public @Nullable V current() {
            return this.current;
        }
    }

    static final class ExpungingEntryImpl<V>
    implements Int2ObjectSyncMap.ExpungingEntry<V> {
        private static final AtomicReferenceFieldUpdater<ExpungingEntryImpl, Object> UPDATER = AtomicReferenceFieldUpdater.newUpdater(ExpungingEntryImpl.class, Object.class, "value");
        private static final Object EXPUNGED = new Object();
        private volatile Object value;

        ExpungingEntryImpl(@NonNull V value) {
            this.value = value;
        }

        @Override
        public boolean exists() {
            return this.value != null && this.value != EXPUNGED;
        }

        @Override
        public @Nullable V get() {
            return (V)(this.value == EXPUNGED ? null : this.value);
        }

        @Override
        public @NonNull V getOr(@NonNull V other) {
            Object value = this.value;
            return (V)(value != null && value != EXPUNGED ? this.value : other);
        }

        @Override
        public @NonNull Int2ObjectSyncMap.InsertionResult<V> setIfAbsent(@NonNull V value) {
            do {
                Object previous;
                if ((previous = this.value) == EXPUNGED) {
                    return new InsertionResultImpl<Object>(2, null, null);
                }
                if (previous == null) continue;
                return new InsertionResultImpl<Object>(0, previous, previous);
            } while (!UPDATER.compareAndSet(this, null, value));
            return new InsertionResultImpl<Object>(1, null, value);
        }

        @Override
        public @NonNull Int2ObjectSyncMap.InsertionResult<V> computeIfAbsent(int key, @NonNull IntFunction<? extends V> function) {
            Object next = null;
            do {
                Object previous;
                if ((previous = this.value) == EXPUNGED) {
                    return new InsertionResultImpl<Object>(2, null, null);
                }
                if (previous == null) continue;
                return new InsertionResultImpl<Object>(0, previous, previous);
            } while (!UPDATER.compareAndSet(this, null, next != null ? next : (next = (Object)function.apply(key))));
            return new InsertionResultImpl<Object>(1, null, next);
        }

        @Override
        public @NonNull Int2ObjectSyncMap.InsertionResult<V> computeIfAbsentPrimitive(int key, @NonNull Int2ObjectFunction<? extends V> function) {
            Object next = null;
            do {
                Object previous;
                if ((previous = this.value) == EXPUNGED) {
                    return new InsertionResultImpl<Object>(2, null, null);
                }
                if (previous == null) continue;
                return new InsertionResultImpl<Object>(0, previous, previous);
            } while (!UPDATER.compareAndSet(this, null, next != null ? next : (next = function.containsKey(key) ? (Object)function.get(key) : null)));
            return new InsertionResultImpl<Object>(1, null, next);
        }

        @Override
        public @NonNull Int2ObjectSyncMap.InsertionResult<V> computeIfPresent(int key, @NonNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
            Object previous;
            Object next = null;
            do {
                if ((previous = this.value) == EXPUNGED) {
                    return new InsertionResultImpl<Object>(2, null, null);
                }
                if (previous != null) continue;
                return new InsertionResultImpl<Object>(0, null, null);
            } while (!UPDATER.compareAndSet(this, previous, next != null ? next : (next = (Object)remappingFunction.apply(key, previous))));
            return new InsertionResultImpl<Object>(1, previous, next);
        }

        @Override
        public @NonNull Int2ObjectSyncMap.InsertionResult<V> compute(int key, @NonNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
            Object previous;
            Object next = null;
            do {
                if ((previous = this.value) != EXPUNGED) continue;
                return new InsertionResultImpl<Object>(2, null, null);
            } while (!UPDATER.compareAndSet(this, previous, next != null ? next : (next = (Object)remappingFunction.apply(key, previous))));
            return new InsertionResultImpl<Object>(previous != next ? (byte)1 : 0, previous, next);
        }

        @Override
        public void set(@NonNull V value) {
            UPDATER.set(this, value);
        }

        @Override
        public boolean replace(@NonNull Object compare, @Nullable V value) {
            Object previous;
            do {
                if ((previous = this.value) != EXPUNGED && Objects.equals(previous, compare)) continue;
                return false;
            } while (!UPDATER.compareAndSet(this, previous, value));
            return true;
        }

        @Override
        public @Nullable V clear() {
            Object previous;
            do {
                if ((previous = this.value) != null && previous != EXPUNGED) continue;
                return null;
            } while (!UPDATER.compareAndSet(this, previous, null));
            return (V)previous;
        }

        @Override
        public boolean trySet(@NonNull V value) {
            Object previous;
            do {
                if ((previous = this.value) != EXPUNGED) continue;
                return false;
            } while (!UPDATER.compareAndSet(this, previous, value));
            return true;
        }

        @Override
        public @Nullable V tryReplace(@NonNull V value) {
            Object previous;
            do {
                if ((previous = this.value) != null && previous != EXPUNGED) continue;
                return null;
            } while (!UPDATER.compareAndSet(this, previous, value));
            return (V)previous;
        }

        @Override
        public boolean tryExpunge() {
            while (this.value == null) {
                if (!UPDATER.compareAndSet(this, null, EXPUNGED)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean tryUnexpungeAndSet(@NonNull V value) {
            return UPDATER.compareAndSet(this, EXPUNGED, value);
        }

        @Override
        public boolean tryUnexpungeAndCompute(int key, @NonNull IntFunction<? extends V> function) {
            if (this.value == EXPUNGED) {
                V value = function.apply(key);
                return UPDATER.compareAndSet(this, EXPUNGED, value);
            }
            return false;
        }

        @Override
        public boolean tryUnexpungeAndComputePrimitive(int key, @NonNull Int2ObjectFunction<? extends V> function) {
            if (this.value == EXPUNGED) {
                Object value = function.containsKey(key) ? (Object)function.get(key) : null;
                return UPDATER.compareAndSet(this, EXPUNGED, value);
            }
            return false;
        }

        @Override
        public boolean tryUnexpungeAndCompute(int key, @NonNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
            if (this.value == EXPUNGED) {
                V value = remappingFunction.apply(key, null);
                return UPDATER.compareAndSet(this, EXPUNGED, value);
            }
            return false;
        }
    }
}

