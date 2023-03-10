/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.click.comp;

import lodomir.dev.modules.Module;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.click.Clickgui;

public class Comp {
    public double x;
    public double y;
    public double x2;
    public double y2;
    public Clickgui parent;
    public Module module;
    public Setting setting;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void drawScreen(int mouseX, int mouseY) {
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (double)mouseX > x && (double)mouseX < x2 && (double)mouseY > y && (double)mouseY < y2;
    }

    public void keyTyped(char typedChar, int keyCode) {
    }
}

