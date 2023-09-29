package de.lirium.base.setting.impl;

import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.ISetting;

public class CheckBox extends ISetting<Boolean> {
    public CheckBox(boolean value, Dependency<?>... dependencies) {
        super(value, dependencies);
    }

    public CheckBox(boolean value) {
        super(value);
    }
}
