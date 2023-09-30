package dev.echo.listener.event.impl.network;

import dev.echo.listener.event.Event;
import net.minecraft.network.Packet;


public class PacketEvent extends Event {
    private Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

   
    public Packet<?> getPacket() {
        return packet;
    }

   
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

   
    public int getPacketID() {
        return getPacket().getID();
    }

}
