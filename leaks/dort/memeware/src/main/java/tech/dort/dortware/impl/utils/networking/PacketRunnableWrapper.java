package tech.dort.dortware.impl.utils.networking;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.dort.dortware.Client;
import tech.dort.dortware.impl.events.PacketEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PacketRunnableWrapper {

    private final PacketRunnable runnable;

    public PacketRunnableWrapper(PacketRunnable runnable, long stop_after) {
        this.runnable = runnable;

        new Thread(() -> {
            Client.INSTANCE.getEventBus().register(PacketRunnableWrapper.this);
            try {
                Thread.sleep(stop_after);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Client.INSTANCE.getEventBus().unregister(PacketRunnableWrapper.this);
        }).start();
    }

    public static final Consumer<PacketEvent> aacExploit = packetEvent -> {
        Minecraft mc = Minecraft.getMinecraft();
        if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packetPlayerPosLook = packetEvent.getPacket();
            double x = packetPlayerPosLook.getX() - mc.thePlayer.posX;
            double y = packetPlayerPosLook.getY() - mc.thePlayer.posY;
            double z = packetPlayerPosLook.getZ() - mc.thePlayer.posZ;
            double diff = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            double distance = 132.5D;
            if (diff <= distance) {
                packetEvent.setCancelled(true);
            }
        } else if (packetEvent.getPacket() instanceof C03PacketPlayer) {
            double y = Math.round(mc.thePlayer.posY / 0.0625) * 0.0625;
            mc.thePlayer.setPosition(mc.thePlayer.posX, y + 0.0625, mc.thePlayer.posZ);
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                Packet packet = new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.renderYawHead, mc.thePlayer.renderPitchHead, true);
                Packet flag = new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 11.2525, mc.thePlayer.posZ, mc.thePlayer.renderYawHead, mc.thePlayer.renderPitchHead, true);
                List<Packet> packetList = Arrays.asList(packet, flag, packet);
                packetList.forEach(PacketUtil::sendPacketNoEvent);
            }
            packetEvent.setCancelled(true);
        } else if (packetEvent.getPacket() instanceof C0BPacketEntityAction) {
            packetEvent.setCancelled(true);
        }
    };

    @Subscribe
    public void onPacket(PacketEvent event) {
        this.getAction().run(event);
    }

    public PacketRunnable getAction() {
        return runnable;
    }

    @FunctionalInterface
    public interface PacketRunnable {
        void run(PacketEvent packet);
    }


}
