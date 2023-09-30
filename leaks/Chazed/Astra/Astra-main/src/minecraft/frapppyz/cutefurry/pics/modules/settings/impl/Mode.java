package frapppyz.cutefurry.pics.modules.settings.impl;

import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.Setting;

import java.util.Arrays;
import java.util.List;

public class Mode extends Setting {

    public int index;
    public List<String> modes;
    public Mod parent;

    public Mode(String name, Mod parent, String defaultMode, String... modes) {
        this.name = name;
        this.modes = Arrays.asList(modes);
        index = this.modes.indexOf(defaultMode);
        this.parent = parent;
    }

    public Mode(String name, String defaultMode, String... modes) {
        this.name = name;
        this.modes = Arrays.asList(modes);
        index = this.modes.indexOf(defaultMode);
        //this.parent = parent;
    }

    public String getMode() {
        return modes.get(index);
    }

    public boolean is(String mode) {
        return index == modes.indexOf(mode);
    }

    public void increment() {
        if(index < modes.size() - 1) {
            index++;
        } else {
            index = 0;
        }
    }

    public void decrement() {
        if(index > 0) {
            index--;
        } else {
            index = modes.size() - 1;
        }
    }

    public String getValueName() {
        return modes.get(index);
    }

    public String getName() {
        return name;
    }

    public void setMode(String mode) {
        boolean found = false;
        for(String s : modes) {
            if(s.equals(mode)) {
                found = true;
            }
        }
        if(found) {
            this.index = this.modes.indexOf(mode);
        }
    }
}