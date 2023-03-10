package store.femboy.spring.impl.module.settings.impl;

import store.femboy.spring.impl.module.settings.Settings;

public final class BooleanSetting extends Settings {
    public boolean shouldBeShown = true;
    public boolean enabled;

    public BooleanSetting(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }
    public BooleanSetting(String name, boolean enabled, ModeSetting parent, String mode) {
        this.name = name;
        this.enabled = enabled;
        this.parent = parent;
        this.mode = mode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

}
