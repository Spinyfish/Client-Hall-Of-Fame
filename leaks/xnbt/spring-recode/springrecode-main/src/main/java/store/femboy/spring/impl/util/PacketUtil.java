package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
public final class PacketUtil {

    static Minecraft mc = Minecraft.getMinecraft();

    public static void sendPacket(Packet p){
        mc.getNetHandler().addToSendQueue(p);
    }

    public static void sendSilentPacket(Packet p){
        mc.getNetHandler().addToSilentSendQueue(p);
    }
}
