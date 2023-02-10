/**
 * @project Myth
 * @author CodeMan
 * @at 20.08.22, 14:13
 */
package dev.myth.settings;

import dev.myth.api.setting.Setting;
import lombok.Getter;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class NumberSetting extends Setting<Double> {

    @Getter private final double min, max, inc;

    private final HashMap<Double, String> valueAliases;
    private String suffix;

    public NumberSetting(String name, double value, double min, double max, double inc) {
        super(name, value);

        this.min = min;
        this.max = max;
        this.inc = inc;
        this.valueAliases = new HashMap<>();
        this.suffix = "";
    }

    public NumberSetting setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public NumberSetting addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    public NumberSetting setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public NumberSetting addValueAlias(double value, String alias) {
        this.valueAliases.put(value, alias);
        return this;
    }

    public String getValueDisplayString() {
        Double value = getValue();
        if(valueAliases.get(getValue()) != null) return valueAliases.get(value);
        if (value % 1 == 0) {
            return value.intValue() + suffix;
        }
        return value + suffix;
    }

    @Override
    public void setValueFromString(String value) {
        setValue(Double.parseDouble(value));
    }

}
