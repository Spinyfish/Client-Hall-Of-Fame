/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 10.10.2022
 */
package de.lirium.util.feature;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.event.EventListener;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

import java.util.concurrent.CopyOnWriteArrayList;

public class BlinkUtil extends EventListener {


    public BlinkUtil() {
        init();
    }

    private static boolean toggled;

    private static final CopyOnWriteArrayList<Packet<? extends INetHandler>> PACKETS = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Class<? extends Packet<? extends INetHandler>>> toCancel = new CopyOnWriteArrayList<>();

    @EventHandler
    public final Listener<PacketEvent> packetEventListener = e -> {
        final Packet<? extends INetHandler> packet = e.packet;

        if (Minecraft.getMinecraft().getConnection() == null) {
            BlinkUtil.toggled = false;
            PACKETS.clear();
            return;
        }

        if (!toggled) return;

        if (toCancel.contains(packet.getClass()))
            e.setCancelled(true);
    };

    public static void setToggled(final boolean toggled, final CopyOnWriteArrayList<Class<? extends Packet<? extends INetHandler>>> toCancel) {
        if (Minecraft.getMinecraft().getConnection() == null) {
            BlinkUtil.toggled = false;
            PACKETS.clear();
            return;
        }
        BlinkUtil.toCancel = toCancel;

        if (toggled != BlinkUtil.toggled) {
            if (!toggled && !PACKETS.isEmpty()) {
                PACKETS.forEach(packet -> Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacketUnlogged(packet));
                System.out.println("Sent " + PACKETS.size() + " packets.");
                PACKETS.clear();
            }
        }
        BlinkUtil.toggled = toggled;
    }

    public static boolean getToggled() {
        return toggled;
    }
}
