package cc.swift.value;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.function.Supplier;

@Getter @Setter
public abstract class Value<T> {
    private final String name;
    private T value;
    private final LinkedList<ValueChangeListener> onChangeListeners = new LinkedList<>();
    protected Supplier<Boolean> dependency;

    protected Value(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public void setValue(T value) {
        this.value = value;
        this.onChangeListeners.forEach(ValueChangeListener::call);
    }

    public Value<T> addChangeListener(ValueChangeListener listener) {
        this.onChangeListeners.add(listener);
        return this;
    }

    public Value<T> setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }

    public boolean isVisible() {
        return dependency == null || dependency.get();
    }

    interface ValueChangeListener {
        void call();
    }
}