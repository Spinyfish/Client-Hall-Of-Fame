package io.github.raze.settings.collection;

import io.github.raze.modules.system.BaseModule;
import io.github.raze.settings.system.BaseSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArraySetting extends BaseSetting {

    public String value;
    public List<String> all;

    public ArraySetting(BaseModule parent, String name, String value, String... all) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.all = new ArrayList<String>(Arrays.asList(all));
    }

    public List<String> all() {
        return all;
    }

    public String get() {
        return value;
    }

    public void set(String value) {
        onChange(this.value, value);
        change(value);
    }


    public void change(String value) {
        this.value = value;
    }

    public void forward() {
        int current = all.indexOf(value);
        int maximum = all.size();

        if (current < maximum - 1) {
            set(all.get(current + 1));
        } else {
            set(all.get(0));
        }

    }

    public void backward() {
        int current = all.indexOf(value);

        if (current > 0) {
            set(all.get(current - 1));
        } else {
            set(all.get(0));
        }

    }

    public boolean compare(String input) {
        return value.equalsIgnoreCase(input);
    }

    public void onChange(String old, String current) { /* */ };

}
