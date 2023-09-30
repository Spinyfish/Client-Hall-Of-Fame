package dev.echo.listener.event.impl.game;

import dev.echo.listener.event.Event;


public class KeyPressEvent extends Event {

    private final int key;

    public KeyPressEvent(int key) {
        this.key = key;
    }

   
    public int getKey() {
        return key;
    }

}
