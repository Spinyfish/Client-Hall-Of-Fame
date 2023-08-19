package dev.thread.api.setting;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

@Setter
public class Setting<T> {
    @Getter
    private String name, description;
    private Supplier<Boolean> visible;
    @Getter
    private T value;
    @Getter
    private Number min, max, increment;

    public Setting(String name, String description, T value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public Setting(String name, String description, T value, Supplier<Boolean> visible) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.visible = visible;
    }

    public Setting(String name, String description, T value, Number min, Number max, Number increment) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public Setting(String name, String description, T value, Number min, Number max, Number increment, Supplier<Boolean> visible) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible == null || visible.get();
    }
}