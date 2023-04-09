package tech.dort.dortware.impl.utils.networking;

import net.minecraft.network.Packet;
import tech.dort.dortware.api.util.Util;

public class PacketUtil implements Util {

    public static void sendPacket(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
}