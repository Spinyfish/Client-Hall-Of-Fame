package me.kansio.client.event.impl;

import me.kansio.client.event.Event;

public class MouseEvent extends Event {
    private int button;

    public MouseEvent(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }
}
