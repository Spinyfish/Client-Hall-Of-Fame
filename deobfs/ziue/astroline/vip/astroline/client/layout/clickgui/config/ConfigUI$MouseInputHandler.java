/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package vip.astroline.client.layout.clickgui.config;

import org.lwjgl.input.Mouse;

public static class ConfigUI.MouseInputHandler {
    public boolean clicked;
    private final int button;

    public ConfigUI.MouseInputHandler(int key) {
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
