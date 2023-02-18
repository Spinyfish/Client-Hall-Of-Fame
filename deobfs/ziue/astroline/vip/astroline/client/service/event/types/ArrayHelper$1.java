/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.event.types;

import java.util.Iterator;

class ArrayHelper.1
implements Iterator<T> {
    private int index = 0;

    ArrayHelper.1() {
    }

    @Override
    public boolean hasNext() {
        return this.index < ArrayHelper.this.size() && ArrayHelper.this.get(this.index) != null;
    }

    @Override
    public T next() {
        return ArrayHelper.this.get(this.index++);
    }

    @Override
    public void remove() {
        ArrayHelper.this.remove(ArrayHelper.this.get(this.index));
    }
}
