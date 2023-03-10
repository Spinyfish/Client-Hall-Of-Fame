/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;

public class EventChat
extends Event {
    public String message;

    public EventChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

