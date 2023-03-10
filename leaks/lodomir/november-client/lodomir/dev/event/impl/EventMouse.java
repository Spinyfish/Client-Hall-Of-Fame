/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;

public class EventMouse
extends Event {
    private int mouseX;
    private int mouseY;
    private int mouseButton;
    private Type type;

    public static enum Type {
        CLICK,
        CLICK_MOVE,
        RELEASED,
        NO_SCREEN;

    }
}

