/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.settings;

import lodomir.dev.settings.Setting;

public class BooleanSetting
extends Setting {
    private boolean enabled;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.enabled = defaultValue;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

