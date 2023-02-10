/**
 * @project Myth
 * @author CodeMan
 * @at 20.08.22, 14:13
 */
package dev.myth.settings;

import dev.myth.api.feature.Feature;
import dev.myth.api.setting.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean value) {
        super(name, value);
    }

    @Override
    public BooleanSetting addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    public BooleanSetting setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public void setValueFromString(String value) {
        setValue(Boolean.parseBoolean(value));
    }
}
