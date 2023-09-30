package dev.tenacity.utils.server;

import dev.tenacity.utils.Utils;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

public class PacketUtils implements Utils {

    public static void sendPacket(Packet<?> packet, boolean silent) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(packet, silent);
        }
    }

    public static void sendPacketNoEvent(Packet packet) {
        sendPacket(packet, true);
    }

    public static void sendPacket(Packet packet) {
        sendPacket(packet, false);
    }

    public static boolean isPacketValid(final Packet packet) {
        return !(packet instanceof C00PacketLoginStart) && !(packet instanceof C00Handshake) && !(packet instanceof C00PacketServerQuery) && !(packet instanceof C01PacketPing);
    }

}
