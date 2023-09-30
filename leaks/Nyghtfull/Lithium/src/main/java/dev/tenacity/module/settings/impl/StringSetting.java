package dev.tenacity.module.settings.impl;

import dev.tenacity.module.settings.Setting;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class StringSetting extends Setting {

    private String string = "";

    public StringSetting(String name) {
        this.name = name;
    }

    public StringSetting(String name, String defaultValue) {
        this.name = name;
        this.string = defaultValue;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public String getString() {
        return string;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String getConfigValue() {
        return string;
    }

}
