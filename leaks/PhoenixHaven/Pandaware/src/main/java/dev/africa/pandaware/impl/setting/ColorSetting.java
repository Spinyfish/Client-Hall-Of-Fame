package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.setting.Setting;

import java.awt.*;
import java.util.function.Supplier;

/*
 * Created by sage on 20/07/2021 at 13:13
 */
public class ColorSetting extends Setting<Color> {
    private Color color;

    public ColorSetting(String name, Color color) {
        super(name, () -> true);
        this.color = color;
    }

    public ColorSetting(String name, Color color, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.color = color;
    }

    @Override
    public Color getValue() {
        return this.color;
    }

    @Override
    public void setValue(Color value) {
        this.color = value;
    }

    @Override
    public ColorSetting setSaveConfig(boolean saveConfig) {
        return (ColorSetting) super.setSaveConfig(saveConfig);
    }
}
