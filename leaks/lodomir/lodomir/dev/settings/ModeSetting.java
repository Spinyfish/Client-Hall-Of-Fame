/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.settings;

import java.util.Arrays;
import java.util.List;
import lodomir.dev.settings.Setting;

public class ModeSetting
extends Setting {
    private String mode;
    private List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String ... modes) {
        super(name);
        this.modes = Arrays.asList(modes);
        this.mode = defaultMode;
        this.index = this.modes.indexOf(defaultMode);
    }

    public String getMode() {
        return this.mode;
    }

    public List<String> getModes() {
        return this.modes;
    }

    public void setMode(String mode) {
        this.mode = mode;
        this.index = this.modes.indexOf(mode);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
        this.mode = this.modes.get(index);
    }

    public void cycle(boolean forwards) {
        if (!forwards) {
            if (this.index < this.modes.size() - 1) {
                ++this.index;
                this.mode = this.modes.get(this.index);
            } else {
                this.index = 0;
                this.mode = this.modes.get(0);
            }
        }
        if (forwards) {
            if (this.index > 0) {
                --this.index;
                this.mode = this.modes.get(this.index);
            } else {
                this.index = this.modes.size() - 1;
                this.mode = this.modes.get(this.index);
            }
        }
    }

    public void cycle() {
        if (this.index < this.modes.size() - 1) {
            ++this.index;
            this.mode = this.modes.get(this.index);
        } else if (this.index >= this.modes.size() - 1) {
            this.index = 0;
            this.mode = this.modes.get(0);
        }
    }

    public void reversecycle() {
        if (this.index > this.modes.size() - 1) {
            ++this.index;
            this.mode = this.modes.get(this.index);
        } else if (this.index <= this.modes.size() - 1) {
            this.index = 0;
            this.mode = this.modes.get(0);
        }
    }

    public boolean isMode(String mode) {
        return this.mode == mode;
    }
}

