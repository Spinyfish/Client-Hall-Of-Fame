package com.alan.clients.component.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.ServerJoinEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import util.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

@Rise
public final class BlinkComponent extends Component {

    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static boolean blinking, dispatch;
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public static StopWatch exemptionWatch = new StopWatch();

    public static void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        if (mc.thePlayer == null) {
            packets.clear();
            exemptedPackets.clear();
            return;
        }

        if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
            packets.forEach(PacketUtil::sendNoEvent);
            packets.clear();
            blinking = false;
            exemptedPackets.clear();
            return;
        }

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                packet instanceof C01PacketEncryptionResponse) {
            return;
        }

        if (blinking && !dispatch) {
            if (exemptionWatch.finished(100)) {
                exemptionWatch.reset();
                exemptedPackets.clear();
            }

            PingSpoofComponent.spoofing = false;

            if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
                    packetClass == packet.getClass())) {
                packets.add(packet);
                event.setCancelled(true);
            }
        } else if (packet instanceof C03PacketPlayer) {
            packets.forEach(PacketUtil::sendNoEvent);
            packets.clear();
            dispatch = false;
        }
    };

    public static void dispatch() {
        dispatch = true;
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        packets.clear();
        BlinkComponent.blinking = false;
    };

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<ServerJoinEvent> onServerJoin = event -> {
        packets.clear();
        BlinkComponent.blinking = false;
    };

}
