package dev.tenacity.module.settings.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.tenacity.module.settings.Setting;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class BooleanSetting extends Setting {

    @Expose
    @SerializedName("name")
    private boolean state;

    public BooleanSetting(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isEnabled() {
        return state;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggle() {
        setState(!isEnabled());
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public Boolean getConfigValue() {
        return isEnabled();
    }

}
