package dev.echo.utils.player;

import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.utils.server.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PingSpoofUtil {
    public static final LinkedList<Packet<?>> packets = new LinkedList<>();
    private static LinkedList<Packet<INetHandlerPlayServer>> packetBuffer = new LinkedList<>();

    public static void spoof(PacketSendEvent event, int delay, boolean C00, boolean C0F, boolean C0B, boolean C13, boolean C16, int packetLoss) {
        Packet packet = event.getPacket();
        if (((packet instanceof C00PacketKeepAlive && C00) || (packet instanceof C0FPacketConfirmTransaction && C0F) ||
                (packet instanceof C0BPacketEntityAction && C0B) || (packet instanceof C13PacketPlayerAbilities && C13) ||
                (packet instanceof C16PacketClientStatus && C16))) {
            event.setCancelled(true);
        }
        if (packetLoss == 0 || Math.random() > packetLoss) {
            packetBuffer.add((Packet<INetHandlerPlayServer>) packet);
            queuePacket(delay);
        }
    }

    private static void queuePacket(long delayTime) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!packetBuffer.isEmpty()) {
                    PacketUtils.sendPacketNoEvent(packetBuffer.poll());
                }
            }
        }, delayTime);
    }

}
