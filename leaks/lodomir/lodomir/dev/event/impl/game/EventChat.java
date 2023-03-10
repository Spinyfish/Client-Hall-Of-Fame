/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.game;

import lodomir.dev.event.EventUpdate;

public class EventChat
extends EventUpdate {
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

