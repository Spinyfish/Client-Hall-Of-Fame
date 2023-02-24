package me.kansio.client.event.impl;

import lombok.Getter;
import me.kansio.client.event.Event;

@Getter
public class KeyboardEvent extends Event {

    private final int keyCode;

    public KeyboardEvent(int key) {
        this.keyCode = key;
    }

}
