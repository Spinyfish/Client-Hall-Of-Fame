/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package vip.astroline.client.layout.clickgui;

import org.lwjgl.input.Mouse;

public static class ClickGUI.MouseHandler {
    public boolean clicked;
    private final int button;

    public ClickGUI.MouseHandler(int key) {
        this.button = key;
    }

    public boolean canExcecute() {
        if (Mouse.isButtonDown((int)this.button)) {
            if (this.clicked) return false;
            this.clicked = true;
            return true;
        }
        this.clicked = false;
        return false;
    }
}
