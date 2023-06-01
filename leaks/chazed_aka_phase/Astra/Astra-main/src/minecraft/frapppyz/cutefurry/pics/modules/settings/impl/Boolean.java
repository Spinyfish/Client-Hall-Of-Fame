package frapppyz.cutefurry.pics.modules.settings.impl;

import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.Setting;

import java.util.Arrays;
import java.util.List;

public class Boolean extends Setting {

    public int index;
    public boolean toggled;
    public Mod parent;

    public Boolean(String name, boolean enabled) {
        this.name = name;
        this.toggled = enabled;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void cycle() {
        toggled = !toggled;
    }

    public String getValueName() {
        return toggled ? "On" : "Off";
    }

    public String getName() {
        return name;
    }
}