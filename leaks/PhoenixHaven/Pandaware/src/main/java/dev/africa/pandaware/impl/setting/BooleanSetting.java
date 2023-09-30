package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.setting.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {
    private boolean value;

    public BooleanSetting(String name, boolean value) {
        super(name, () -> true);
        this.value = value;
    }

    public BooleanSetting(String name, boolean value, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public BooleanSetting setSaveConfig(boolean saveConfig) {
        return (BooleanSetting) super.setSaveConfig(saveConfig);
    }
}
