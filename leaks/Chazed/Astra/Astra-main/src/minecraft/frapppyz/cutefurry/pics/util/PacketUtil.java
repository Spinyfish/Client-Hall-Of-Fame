package frapppyz.cutefurry.pics.util;

import frapppyz.cutefurry.pics.event.impl.SendPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtil {
    public static void sendPacket(Packet p){
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(p);
    }
    public static void sendPacketNoEvent(Packet p){Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(p);}

    public static void receivePacket(Packet p){
        p.processPacket(Minecraft.getMinecraft().getNetHandler());
    }

    public static void receiveDelayedPacket(Packet p, int millis){
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            receivePacket(p);
        }).start();

    }

    public static void sendDelayedPacket(Packet p, int millis){
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            sendPacket(p);
        }).start();
    }

    public static void sendDelayedPacketNoEvent(Packet p, int millis){
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            sendPacketNoEvent(p);
        }).start();
    }

}
