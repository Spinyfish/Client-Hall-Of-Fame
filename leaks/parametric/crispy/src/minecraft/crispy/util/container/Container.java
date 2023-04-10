package crispy.util.container;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Container<T> {

    @Getter
    @Setter
    private List<T> items = new CopyOnWriteArrayList<>();

    /**
     * Adds an item to the container
     *
     * @param item the item that gets added
     */

    public void add(T item) {
        items.add(item);
    }

    /**
     * Adds multiple items at once
     *
     * @param items the items
     */

    @SafeVarargs
    public final void add(T... items) {
        Arrays.stream(items).forEach(this::add);
    }

    /**
     * Removes an item from the container
     *
     * @param item the item that gets added
     */

    public void remove(T item) {
        items.remove(item);
    }

    /**
     * Fetches an item from items
     * at the given index and it
     * returns it
     *
     * @param index wanted index of the item
     * @return the item at that index
     */

    public T get(int index) {
        try {
            return items.get(index);
        } catch (Exception e) {
            return items.get(0);
        }
    }

    /**
     * Fetches the index of the item
     * that you provide
     *
     * @param item the target item
     * @return index of the given item
     */

    public int indexOf(T item) {
        return items.indexOf(item);
    }

    /**
     * Checks if there is any items
     * in the container
     *
     * @return true/false depending if it found any items or not
     */

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Loops through all items
     * and applies an action to them
     *
     * @param action that you want to apply
     */

    public void forEach(Consumer<? super T> action) {
        getItems().forEach(action);
    }

    /**
     * Creates an item stream
     * and returns it
     *
     * @return stream of items
     */

    public Stream<T> stream() {
        return getItems().stream();
    }

    /**
     * Applies the filter to the items
     * and if its matching returns the
     * matched item stream
     *
     * @param predicate that has to match
     * @return a stream of matched items
     */

    public Stream<T> filter(Predicate<? super T> predicate) {
        return stream().filter(predicate);
    }

    /**
     * Tries to find an item
     * matching the given predicate
     *
     * @param predicate target predicate
     * @return an item
     */

    public T find(Predicate<? super T> predicate) {
        return filter(predicate).findFirst().orElse(null);
    }

    /**
     * Tries to find an item
     * with the matching class
     *
     * @param aClass target class
     * @return found item
     */

    public T findByClass(Class<? extends T> aClass) {
        return stream().filter(item -> item.getClass().equals(aClass)).findFirst().orElse(null);
    }

}