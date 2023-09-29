package me.jinthium.straight.impl.event.game;

import me.jinthium.straight.api.event.Event;

public class WindowClickEvent extends Event {

    private final int windowId, slotId, mouseButton, mode;

    public WindowClickEvent(int windowId, int slotId, int mouseButton, int mode) {
        this.windowId = windowId;
        this.slotId = slotId;
        this.mouseButton = mouseButton;
        this.mode = mode;
    }

    public int getWindowId() {
        return windowId;
    }

    public int getSlotId() {
        return slotId;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public int getMode() {
        return mode;
    }
}