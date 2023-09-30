package dev.echo.listener.event.impl.player;

import dev.echo.listener.event.Event;


public class PlayerSendMessageEvent extends Event {
    private final String message;

    public PlayerSendMessageEvent(String message) {
        this.message = message;
    }

   
    public String getMessage() {
        return message;
    }

}
