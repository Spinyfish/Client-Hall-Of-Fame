/**
 * @project Myth
 * @author CodeMan
 * @at 04.01.23, 12:33
 */
package dev.myth.settings;

import dev.myth.api.setting.Setting;

import java.util.function.Supplier;

public class TextBoxSetting extends Setting<String> {
    public TextBoxSetting(String name, String value) {
        super(name, value);
    }

    public TextBoxSetting setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public TextBoxSetting addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
