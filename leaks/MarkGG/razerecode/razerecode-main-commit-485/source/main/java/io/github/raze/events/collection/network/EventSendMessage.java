package io.github.raze.events.collection.network;

public class EventSendMessage {

    public String message;

    public EventSendMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
