package io.github.raze.modules.system;

import io.github.raze.Raze;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.utilities.system.BaseUtility;

public abstract class BaseModule implements BaseUtility {

    public String name;
    public String description;
    public ModuleCategory category;

    public int keyCode;

    public boolean enabled;
    public boolean hidden;

    public BaseModule(String name, String description, ModuleCategory category, int keyCode) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keyCode = keyCode;
    }

    public BaseModule(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable() { /* */ };
    public void onDisable() { /* */ };
    public void onEvent(Object event) { /* */ };

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (isEnabled()) {
            Raze.INSTANCE.MANAGER_REGISTRY.EVENT_REGISTRY.register(this);
            onEnable();
        } else {
            Raze.INSTANCE.MANAGER_REGISTRY.EVENT_REGISTRY.unregister(this);
            onDisable();
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }
}
