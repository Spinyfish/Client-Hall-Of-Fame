package me.jinthium.straight.impl.utils.network;

import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.network.Packet;

public class PacketUtil implements MinecraftInstance {

    public static void sendPacket(Packet<?> packet){
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet<?> packet){
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }

    public static void receivePacket(final Packet<?> packet) {
        mc.getNetHandler().addToReceiveQueue(packet);
    }

    public static void receivePacketNoEvent(final Packet<?> packet) {
        mc.getNetHandler().addToReceiveQueueUnregistered(packet);
    }

    public record TimedPacket(Packet<?> packet, long time) {
    }
}
