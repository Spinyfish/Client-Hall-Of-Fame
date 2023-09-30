package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.setting.Setting;

import java.util.function.Supplier;

public class TextBoxSetting extends Setting<String> {
    private String value;

    public TextBoxSetting(String name, String defaultValue) {
        super(name, () -> true);
        this.value = defaultValue;
    }

    public TextBoxSetting(String name, String defaultValue, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.value = defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public TextBoxSetting setSaveConfig(boolean saveConfig) {
        return (TextBoxSetting) super.setSaveConfig(saveConfig);
    }
}
