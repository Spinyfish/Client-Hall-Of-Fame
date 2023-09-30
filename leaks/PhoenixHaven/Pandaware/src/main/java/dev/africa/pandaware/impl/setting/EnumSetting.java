package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.setting.Setting;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/*
 * Created by sage on 26/07/2021 at 13:06
 */
@Getter
public class EnumSetting<T extends Enum> extends Setting<T> {
    private final Enum<?>[] values;
    private T value;

    public EnumSetting(String name, T value, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.values = value.getClass().getEnumConstants();
        this.value = value;
    }

    public EnumSetting(String name, T value) {
        super(name, () -> true);
        this.values = value.getClass().getEnumConstants();
        this.value = value;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    public String getLabel(T value) {
        return value.toString()/*StringUtils.capitalize(value.name().replace("_", " ").toLowerCase())*/;
    }

    @Override
    public EnumSetting setSaveConfig(boolean saveConfig) {
        return (EnumSetting) super.setSaveConfig(saveConfig);
    }
}
