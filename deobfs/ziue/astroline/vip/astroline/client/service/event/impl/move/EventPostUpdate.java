/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.move;

import vip.astroline.client.service.event.Event;

public class EventPostUpdate
extends Event {
    public float yaw;
    public float pitch;

    public EventPostUpdate(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }
}
