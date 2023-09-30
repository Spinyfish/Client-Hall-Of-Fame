package club.pulsive.impl.property;

public interface ValueChangeListener<T> {
    void onValueChange(T oldValue, T value);
}