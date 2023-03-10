/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.clickgui.impl.settings;

import lodomir.dev.settings.Setting;

public class SetBase {
    public String name;
    public Setting setting;

    public double drawScreen(int mouseX, int mouseY, float partialTicks, double settingHeight) {
        return 0.0;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public boolean isHidden() {
        return false;
    }

    public double getHeight() {
        return 0.0;
    }

    public boolean canRender() {
        return this.setting.isVisible();
    }
}

