package com.alan.clients.component.impl.performance;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.chat.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;

public class ParticleDistanceComponent extends Component {

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S2APacketParticles) {
            final S2APacketParticles wrapper = ((S2APacketParticles) packet);

            final double distance = mc.thePlayer.getDistanceSq(wrapper.getXCoordinate(), wrapper.getYCoordinate(), wrapper.getZCoordinate());

            if (distance > 6 * 6) {
                event.setCancelled(true);
            }
        }
    };
}