/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.packet;

import net.minecraft.network.Packet;
import vip.astroline.client.service.event.Event;

public class EventSendPacket
extends Event {
    public Packet packet;
    private float yaw;
    private float pitch;

    public EventSendPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public void setRotation(float yaw, float pitch) {
        if (Float.isNaN(yaw)) return;
        if (Float.isNaN(pitch)) return;
        if (pitch > 90.0f) return;
        if (pitch < -90.0f) {
            return;
        }
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setRotations(float[] rotations) {
        this.setRotation(rotations[0], rotations[1]);
    }
}
