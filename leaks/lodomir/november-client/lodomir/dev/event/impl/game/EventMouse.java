/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.game;

import lodomir.dev.event.EventUpdate;

public class EventMouse
extends EventUpdate {
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

