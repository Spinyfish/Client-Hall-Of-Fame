/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import packet.Packet;
import wtf.monsoon.api.event.Event;

public class EventBackendPacket
extends Event {
    private Packet packet;

    public EventBackendPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

