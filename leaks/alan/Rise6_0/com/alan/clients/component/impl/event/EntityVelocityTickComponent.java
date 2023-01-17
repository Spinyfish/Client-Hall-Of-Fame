package com.alan.clients.component.impl.event;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@Rise
public class EntityVelocityTickComponent extends Component {

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity && mc != null && mc.theWorld != null) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            Entity entity = mc.theWorld.getEntityByID(wrapper.getEntityID());

            if (entity == null) {
                return;
            }

            entity.ticksSinceVelocity = 0;
        }
    };
}
