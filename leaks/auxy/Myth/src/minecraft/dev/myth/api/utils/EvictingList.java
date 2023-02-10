/**
 * @project Myth
 * @author CodeMan
 * @at 18.09.22, 15:59
 */
package dev.myth.api.utils;

import java.util.ArrayList;

public class EvictingList<T> extends ArrayList<T> {

    private final int maxSize;

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) {
            remove(0);
        }
        return super.add(t);
    }

    public int getMaxSize() {
        return maxSize;
    }

}
