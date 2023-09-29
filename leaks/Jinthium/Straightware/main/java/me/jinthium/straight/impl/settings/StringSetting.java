package me.jinthium.straight.impl.settings;

import imgui.type.ImString;
import me.jinthium.straight.api.setting.Setting;

public class StringSetting extends Setting {

    private String string = "";
    private final ImString imEquiv;

    public StringSetting(String name) {
        this.name = name;
        this.imEquiv = new ImString(string, 1000);
    }

    public StringSetting(String name, String defaultValue) {
        this.name = name;
        this.string = defaultValue;
        this.imEquiv = new ImString(string, 1000);
    }

    
    public String getString() {
        return string;
    }

    public ImString getImEquiv() {
        return imEquiv;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String getConfigValue() {
        return string;
    }

}