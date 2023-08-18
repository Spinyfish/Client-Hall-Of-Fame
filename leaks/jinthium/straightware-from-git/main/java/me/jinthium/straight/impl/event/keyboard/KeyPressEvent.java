package me.jinthium.straight.impl.event.keyboard;

import me.jinthium.straight.api.event.Event;

public class KeyPressEvent extends Event {
    private final int key;
    private final boolean isInsideGui;

    public KeyPressEvent(int key, boolean isInsideGui) {
        this.key = key;
        this.isInsideGui = isInsideGui;
    }

    public int getKey() {
        return key;
    }

    public boolean isInsideGui() {
        return isInsideGui;
    }
}
