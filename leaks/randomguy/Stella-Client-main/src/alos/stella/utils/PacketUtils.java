/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package alos.stella.utils;

import alos.stella.event.EventTarget;
import alos.stella.event.Listenable;
import alos.stella.event.events.PacketEvent;
import alos.stella.event.events.TickEvent;
import alos.stella.utils.timer.MSTimer;

import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import java.util.ArrayList;

public class PacketUtils extends MinecraftInstance implements Listenable {

    public static int inBound, outBound = 0;
    public static int avgInBound, avgOutBound = 0;

    private static ArrayList<Packet<INetHandlerPlayServer>> packets = new ArrayList<Packet<INetHandlerPlayServer>>();

    private static MSTimer packetTimer = new MSTimer();
    private static MSTimer wdTimer = new MSTimer();

    private static int transCount = 0;
    private static int wdVL = 0;

    private static boolean isInventoryAction(short action) {
        return action > 0 && action < 100;
    }

    public static boolean isWatchdogActive() {
        return wdVL >= 8;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        handlePacket(event.getPacket());
    }

    private static void handlePacket(Packet<?> packet) {
        if (packet.getClass().getSimpleName().startsWith("C")) outBound++;
        else if (packet.getClass().getSimpleName().startsWith("S")) inBound++;

        if (packet instanceof S32PacketConfirmTransaction) 
        {
            if (!isInventoryAction(((S32PacketConfirmTransaction) packet).getActionNumber())) 
                transCount++;
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (packetTimer.hasTimePassed(1000L)) {
            avgInBound = inBound; avgOutBound = outBound;
            inBound = outBound = 0;
            packetTimer.reset();
        }
        if (mc.thePlayer == null || mc.theWorld == null) {
            //reset all checks
            wdVL = 0;
            transCount = 0;
            wdTimer.reset();
        } else if (wdTimer.hasTimePassed(100L)) { // watchdog active when the transaction poll rate reaches about 100ms/packet.
            wdVL += (transCount > 0) ? 1 : -1;
            transCount = 0;
            if (wdVL > 10) wdVL = 10;
            if (wdVL < 0) wdVL = 0;
            wdTimer.reset();
        }
    }

    /*
     * This code is from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
     */
    public static void sendPacketNoEvent(Packet<INetHandlerPlayServer> packet) {
        packets.add(packet);
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static boolean handleSendPacket(Packet<?> packet) {
        if (packets.contains(packet)) {
            packets.remove(packet);
            handlePacket(packet); // make sure not to skip silent packets.
            return true;
        }
        return false;
    }

    /**
     * @return wow
     */
    @Override
    public boolean handleEvents() {
        return true;
    }

    public static void send(final Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    private boolean isServerPacket(final Packet<?> packet) {
        return packet.toString().toCharArray()[34] == 'S';
    }

    private boolean isClientPacket(final Packet<?> packet) {
        return packet.toString().toCharArray()[34] == 'C';
    }

    public class TimedPacket {
        private final Packet<?> packet;
        private final long time;

        public TimedPacket(final Packet<?> packet, final long time) {
            this.packet = packet;
            this.time = time;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public long getTime() {
            return time;
        }
    }
    
}