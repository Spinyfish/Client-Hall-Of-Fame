package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.api.setting.Setting;
import lombok.Getter;

import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting<ModuleMode<?>> {
    @Getter
    private final List<ModuleMode<?>> values;
    private ModuleMode<?> value;

    public ModeSetting(String name, List<ModuleMode<?>> values, ModuleMode<?> value) {
        super(name, () -> true);
        this.values = values;
        this.value = value;
    }

    public ModeSetting(String name, List<ModuleMode<?>> values,
                       ModuleMode<?> value, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.values = values;
        this.value = value;
    }

    @Override
    public ModuleMode<?> getValue() {
        return this.value;
    }

    @Override
    public void setValue(ModuleMode<?> value) {
        this.value = value;
    }
}
