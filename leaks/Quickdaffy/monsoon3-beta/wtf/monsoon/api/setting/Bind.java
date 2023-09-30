/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.api.setting;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Bind {
    private int buttonCode;
    private Device device;

    public Bind(int buttonCode, Device device) {
        this.buttonCode = buttonCode;
        this.device = device;
    }

    public boolean isPressed() {
        if (this.buttonCode == 0) {
            return false;
        }
        boolean pressed = Keyboard.isKeyDown((int)this.buttonCode) && this.device.equals((Object)Device.KEYBOARD) || Mouse.isButtonDown((int)this.buttonCode) && this.device.equals((Object)Device.MOUSE);
        return pressed;
    }

    public String getButtonName() {
        return this.device.equals((Object)Device.KEYBOARD) ? Keyboard.getKeyName((int)this.buttonCode) : Mouse.getButtonName((int)this.buttonCode);
    }

    public int getButtonCode() {
        return this.buttonCode;
    }

    public void setButtonCode(int buttonCode) {
        this.buttonCode = buttonCode;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public static enum Device {
        KEYBOARD,
        MOUSE;

    }
}

