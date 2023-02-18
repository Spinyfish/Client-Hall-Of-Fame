/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.event.types;

import java.util.Iterator;

public class ArrayHelper<T>
implements Iterable<T> {
    private T[] elements;

    public ArrayHelper(T[] array) {
        this.elements = array;
    }

    public ArrayHelper() {
        this.elements = new Object[0];
    }

    public void add(T t) {
        if (t == null) return;
        Object[] array = new Object[this.size() + 1];
        int i = 0;
        while (true) {
            if (i >= array.length) {
                this.set(array);
                return;
            }
            array[i] = i < this.size() ? this.get(i) : t;
            ++i;
        }
    }

    public boolean contains(T t) {
        T[] array = this.array();
        int lenght = array.length;
        int i = 0;
        while (i < lenght) {
            T entry = array[i];
            if (entry.equals(t)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public void remove(T t) {
        if (!this.contains(t)) return;
        Object[] array = new Object[this.size() - 1];
        boolean b = true;
        int i = 0;
        while (true) {
            if (i >= this.size()) {
                this.set(array);
                return;
            }
            if (b && this.get(i).equals(t)) {
                b = false;
            } else {
                array[b ? i : i - 1] = this.get(i);
            }
            ++i;
        }
    }

    public T[] array() {
        return this.elements;
    }

    public int size() {
        return this.array().length;
    }

    public void set(T[] array) {
        this.elements = array;
    }

    public T get(int index) {
        return this.array()[index];
    }

    public void clear() {
        this.elements = new Object[0];
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }
}
