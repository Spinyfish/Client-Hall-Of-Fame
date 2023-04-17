package me.levansj01.verus.util.java;

import java.util.stream.*;
import java.util.*;

public interface BasicDeque extends Iterable {
    void addFirst(final Object p0);

    Object pollFirst();

    void clear();

    Object peekLast();

    default Stream stream() {
        return StreamSupport.stream(Spliterators.spliterator(this.iterator(), this.size(), 16), false);
    }

    int size();

    Object pollLast();

    default List toList() {
        final ArrayList<Object> list = new ArrayList<>(this.size());
        this.forEach(list::add);
        return list;
    }

    void addLast(final Object p0);

    Object peekFirst();
}
