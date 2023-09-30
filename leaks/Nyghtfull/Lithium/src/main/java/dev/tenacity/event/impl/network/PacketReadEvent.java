package dev.tenacity.event.impl.network;

import dev.tenacity.event.Event;
import net.minecraft.network.Packet;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketReadEvent extends Event {
    private Packet<?> packet;
    private final INetHandler netHandler;
    private final EnumPacketDirection direction;

    public PacketReadEvent(final Packet<?> packet, final INetHandler netHandler, final EnumPacketDirection direction) {
        this.packet = packet;
        this.netHandler = netHandler;
        this.direction = direction;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(final Packet<?> packet) {
        this.packet = packet;
    }

    public INetHandler getNetHandler() {
        return this.netHandler;
    }

    public EnumPacketDirection getDirection() {
        return this.direction;
    }

}
