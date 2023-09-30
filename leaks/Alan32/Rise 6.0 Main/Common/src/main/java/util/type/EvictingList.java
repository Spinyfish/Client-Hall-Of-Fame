package util.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public class EvictingList<T> extends LinkedList<T> implements Serializable {

    @Getter
    @Setter
    private int maxSize;

    public EvictingList(final int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingList(final Collection<? extends T> c, final int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(final T t) {
        if (size() >= getMaxSize()) removeFirst();
        return super.add(t);
    }

    public boolean isFull() {
        return size() >= getMaxSize();
    }
}