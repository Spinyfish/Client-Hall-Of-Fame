package me.kansio.client.utils.network;

import me.kansio.client.utils.Util;
import net.minecraft.network.Packet;

public class PacketUtil extends Util {

    public static void sendPacket(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        mc.getNetHandler().addToSendQueueNoEvent(packet);
    }

    public static void sendPacket(int sendTimes, Packet packet) {
        for (int i = 0; i < sendTimes; i++) {
            mc.getNetHandler().addToSendQueue(packet);
        }
    }

    public static void sendPacketNoEvent(int sendTimes, Packet packet) {
        for (int i = 0; i < sendTimes; i++) {
            mc.getNetHandler().addToSendQueueNoEvent(packet);
        }
    }
}
