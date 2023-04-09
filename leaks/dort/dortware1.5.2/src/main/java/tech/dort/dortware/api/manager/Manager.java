package tech.dort.dortware.api.manager;

import java.util.List;
import java.util.NoSuchElementException;

public abstract class Manager<T> {

    private final List<T> list;

    protected Manager(List<T> list) {
        this.list = list;
        onCreated();
    }

    /**
     * Adds an object.
     *
     * @param object - The {@code Object} to add
     */
    public final void add(T object) {
        list.add(object);
    }

    /**
     * Gets an object.
     *
     * @param index - The index to get the {@code Object} from.
     * @return The {@code Object} at {@param index}
     */
    public final T get(int index) {
        return list.get(index);
    }

    /**
     * Gets an object.
     */
    @SuppressWarnings("all")
    public <E extends T> E get(Class<? extends T> clazz) {
        return (E) list.stream().filter(element -> element.getClass().equals(clazz)).findFirst().orElseThrow((() -> new NoSuchElementException("RETARD ALERT: Element belonging to class '" + clazz.getName() + "' not found")));
    }

    /**
     * @return The list
     */
    public List<T> getObjects() {
        return list;
    }

    /**
     * Called by this {@code Manager}'s constructor in order to setup everything.
     */
    public void onCreated() {

    }
}
