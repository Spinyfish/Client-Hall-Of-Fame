package io.github.raze.settings.system;

import io.github.raze.modules.system.BaseModule;

public class BaseSetting {

    public String name;
    public String description;

    public BaseModule parent;
    public boolean hidden;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public BaseModule getParent() {
        return parent;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
