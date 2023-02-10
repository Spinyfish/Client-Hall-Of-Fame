/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 16:21
 */
package dev.myth.api.setting;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class Setting<T> {

    @Getter private final String name;
    @Getter protected String displayName;
    @Getter @Setter private T value;

    protected Supplier<Boolean> visible;

    public Setting(String name, T value) {
        this.name = name;
        this.displayName = name;
        this.value = value;
    }

    public Setting<T> setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public void setValueFromString(String value) {

    }

    public Setting<T> addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    public boolean isVisible() {
        if(visible == null) return true;
        return visible.get();
    }
}
