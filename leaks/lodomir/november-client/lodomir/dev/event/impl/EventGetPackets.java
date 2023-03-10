/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;
import net.minecraft.network.Packet;

public class EventGetPackets
extends Event {
    public Packet packets;

    public EventGetPackets(Packet p) {
        this.packets = p;
    }

    public Packet getPackets() {
        return this.packets;
    }

    public void setPackets(Packet packets) {
        this.packets = packets;
    }
}

