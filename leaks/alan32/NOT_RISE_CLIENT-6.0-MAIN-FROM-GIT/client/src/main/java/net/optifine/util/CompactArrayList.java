package net.optifine.util;

import java.util.ArrayList;

public class CompactArrayList {
    private ArrayList list;
    private int initialCapacity;
    private float loadFactor;
    private int countValid;

    public CompactArrayList() {
        this(10, 0.75F);
    }

    public CompactArrayList(final int initialCapacity) {
        this(initialCapacity, 0.75F);
    }

    public CompactArrayList(final int initialCapacity, final float loadFactor) {
        this.list = null;
        this.initialCapacity = 0;
        this.loadFactor = 1.0F;
        this.countValid = 0;
        this.list = new ArrayList(initialCapacity);
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    public void add(final int index, final Object element) {
        if (element != null) {
            ++this.countValid;
        }

        this.list.add(index, element);
    }

    public boolean add(final Object element) {
        if (element != null) {
            ++this.countValid;
        }

        return this.list.add(element);
    }

    public Object set(final int index, final Object element) {
        final Object object = this.list.set(index, element);

        if (element != object) {
            if (object == null) {
                ++this.countValid;
            }

            if (element == null) {
                --this.countValid;
            }
        }

        return object;
    }

    public Object remove(final int index) {
        final Object object = this.list.remove(index);

        if (object != null) {
            --this.countValid;
        }

        return object;
    }

    public void clear() {
        this.list.clear();
        this.countValid = 0;
    }

    public void compact() {
        if (this.countValid <= 0 && this.list.size() <= 0) {
            this.clear();
        } else if (this.list.size() > this.initialCapacity) {
            final float f = (float) this.countValid * 1.0F / (float) this.list.size();

            if (f <= this.loadFactor) {
                int i = 0;

                for (int j = 0; j < this.list.size(); ++j) {
                    final Object object = this.list.get(j);

                    if (object != null) {
                        if (j != i) {
                            this.list.set(i, object);
                        }

                        ++i;
                    }
                }

                for (int k = this.list.size() - 1; k >= i; --k) {
                    this.list.remove(k);
                }
            }
        }
    }

    public boolean contains(final Object elem) {
        return this.list.contains(elem);
    }

    public Object get(final int index) {
        return this.list.get(index);
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public int getCountValid() {
        return this.countValid;
    }
}
