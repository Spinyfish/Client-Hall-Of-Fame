package me.kansio.client.event.impl;

import me.kansio.client.event.Event;

public class ChatEvent extends Event {

    private final String message;


    public ChatEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
