/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.settings;

public class Setting {
    private String name;
    public boolean visible = true;

    public Setting(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return this.name;
    }
}

