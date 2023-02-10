/**
 * @project Myth
 * @author CodeMan
 * @at 20.08.22, 14:13
 */
package dev.myth.settings;

import dev.myth.api.setting.Setting;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Supplier;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {

    @Getter private final T[] constants;

    public EnumSetting(String name, Enum<T> value) {
        super(name, (T) value);

        this.constants = value.getDeclaringClass().getEnumConstants();
    }

    public EnumSetting<T> setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public void setValueFromString(String value) {
        this.setValue(Arrays.stream(this.constants).filter(e -> e.name().equalsIgnoreCase(value)).findFirst().orElse(this.getValue()));
    }

    public boolean is(T value) {
        return this.getValue() == value;
    }

    public EnumSetting<T> addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    public String[] getValues() {
        return Arrays.stream(constants).map(Enum::toString).toArray(String[]::new);
    }

    public void nextValue() {
        int index = Arrays.asList(constants).indexOf(getValue());
        if(index == -1) return;
        if(index == constants.length - 1) index = 0;
        else index++;
        setValue(constants[index]);
    }

    public void previousValue() {
        int index = Arrays.asList(constants).indexOf(getValue());
        if(index == -1) return;
        if(index == 0) index = constants.length - 1;
        else index--;
        setValue(constants[index]);
    }

    public void setValueByIndex(int index) {
        if(constants.length <= index) return;
        setValue(constants[index]);
    }
}
