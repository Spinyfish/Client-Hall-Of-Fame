package cc.swift.util;

import net.minecraft.network.Packet;

public class PacketUtil implements IMinecraft{

    public static void sendPacketNoEvent(Packet p){
        mc.getNetHandler().getNetworkManager().sendPacket(p, null);
    }
    public static void sendPacketNoEventDelayed(Packet p, long delay){
        //Create a new thread that sends a packet after a delay
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                sendPacketNoEvent(p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


}
