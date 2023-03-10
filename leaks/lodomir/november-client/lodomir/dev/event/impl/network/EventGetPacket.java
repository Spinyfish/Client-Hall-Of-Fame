/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.network;

import lodomir.dev.event.EventUpdate;
import net.minecraft.network.Packet;

public class EventGetPacket
extends EventUpdate {
    public Packet packet;

    public EventGetPacket(Packet p) {
        this.packet = p;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packets) {
        this.packet = packets;
    }
}

