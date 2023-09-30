package dev.africa.pandaware.utils.java;

import java.util.ArrayList;

public class EvictingList<E> extends ArrayList<E> {
    private final boolean insertExisting;
    private final int maxSize;

    public EvictingList(int maxSize) {
        this(false, maxSize);
    }

    public EvictingList(boolean insertExisting, int maxSize) {
        this.insertExisting = insertExisting;
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e) {
        if (!this.insertExisting && this.contains(e)){
            return false;
        }

        boolean value = super.add(e);

        while (this.size() > this.maxSize) {
            super.remove(0);
        }

        return value;
    }

    public E getOldest() {
        return this.get(0);
    }

    public E getLatest() {
        return this.get(this.size() - 1);
    }
}