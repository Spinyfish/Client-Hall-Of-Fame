package club.pulsive.impl.util.network;

import club.pulsive.api.minecraft.MinecraftUtil;
import net.minecraft.network.Packet;

public class PacketUtil implements MinecraftUtil {
    
    
    public static void sendPacketNoEventTimes(Packet packet, int times){
        for(int i = 0; i < times; i++){
            sendPacketNoEvent(packet);
        }
    }
    
    public static void sendPacketTimes(Packet packet, int times){
        for(int i = 0; i < times; i++){
            sendPacket(packet);
        }
    }
    
    public static void sendPacketNoEvent(Packet packet){
        mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent(packet);
    }
    
    public static void sendPacket(Packet packet){
        mc.getNetHandler().addToSendQueue(packet);
    }
}
