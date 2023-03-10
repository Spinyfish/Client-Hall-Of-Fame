/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;
import net.minecraft.network.Packet;

public class EventSendPackets
extends Event {
    public Packet packets;

    public EventSendPackets(Packet p) {
        this.packets = p;
    }

    public Packet getPackets() {
        return this.packets;
    }

    public void setPackets(Packet packets) {
        this.packets = packets;
    }
}

