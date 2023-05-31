package de.lirium.base.helper;

import com.viaversion.viaversion.util.Triple;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OverlappingHelper<T> {
    public List<T> elements = new ArrayList<>();

    public void init(List<T> list) {
        this.elements.clear();
        this.elements.addAll(list);
    }

    public T getInFront(int mouseX, int mouseY, Predicate<Triple<Integer, Integer, T>> predicate) {
        T front = null;
        for(T element : this.elements) {
            if(predicate.test(new Triple<>(mouseX, mouseY, element))) {
                front = element;
            }
        }
        return front;
    }

    public List<T> resort(T element) {
        final List<T> sorting = new ArrayList<>();
        this.elements.forEach(t -> {
            if(!t.equals(element))
                sorting.add(t);
        });
        sorting.add(element);
        this.elements = sorting;
        return this.elements;
    }
}
