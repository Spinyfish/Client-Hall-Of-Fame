/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.setting.Setting;

public class EventUpdateEnumSetting
extends Event {
    private Setting<Enum<?>> setting;
    private Enum<?> oldValue;
    private Enum<?> newValue;

    public EventUpdateEnumSetting(Setting<Enum<?>> setting, Enum<?> oldValue, Enum<?> newValue) {
        this.setSetting(setting);
        this.setOldValue(oldValue);
        this.setNewValue(newValue);
    }

    public Setting<Enum<?>> getSetting() {
        return this.setting;
    }

    public Enum<?> getOldValue() {
        return this.oldValue;
    }

    public Enum<?> getNewValue() {
        return this.newValue;
    }

    public void setSetting(Setting<Enum<?>> setting) {
        this.setting = setting;
    }

    public void setOldValue(Enum<?> oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(Enum<?> newValue) {
        this.newValue = newValue;
    }
}

