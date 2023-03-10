/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.game;

import lodomir.dev.event.EventUpdate;

public class EventKey
extends EventUpdate {
    public int key;

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public EventKey(int key) {
        this.key = key;
    }
}

