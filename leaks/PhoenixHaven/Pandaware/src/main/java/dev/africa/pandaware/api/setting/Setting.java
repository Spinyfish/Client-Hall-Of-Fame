package dev.africa.pandaware.api.setting;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

@Getter
@Setter
public abstract class Setting<T> {
    private final String name;
    private Supplier<Boolean> supplier;
    private boolean saveConfig;

    public Setting(String name, Supplier<Boolean> supplier) {
        this.name = name;
        this.supplier = supplier;
        this.saveConfig = true;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public Setting<?> setSaveConfig(boolean saveConfig) {
        this.saveConfig = saveConfig;
        return this;
    }
}
