package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class EventChat extends Event {
    private String rawMessage;

    public String getRawMessage() {
        return this.rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public EventChat(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}
