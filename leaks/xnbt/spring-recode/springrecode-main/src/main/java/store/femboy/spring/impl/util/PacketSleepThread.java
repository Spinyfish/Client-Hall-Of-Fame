package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public final class PacketSleepThread extends Thread {

    public PacketSleepThread(Packet packet, long delay) {
        super(() -> {
            sleep_ms(delay);
            if (Minecraft.getMinecraft().thePlayer != null)
                PacketUtil.sendSilentPacket(packet);
        });
    }

    static void sleep_ms(long delay) {
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void delayPacket(Packet packet, long delay) {
        new PacketSleepThread(packet, delay).start();
    }

}
