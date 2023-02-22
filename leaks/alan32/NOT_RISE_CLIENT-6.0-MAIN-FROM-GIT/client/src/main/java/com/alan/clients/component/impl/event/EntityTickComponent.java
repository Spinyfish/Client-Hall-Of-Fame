package com.alan.clients.component.impl.event;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@Rise
public class EntityTickComponent extends Component {

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        if (mc == null || mc.theWorld == null) return;

        Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            Entity entity = mc.theWorld.getEntityByID(wrapper.getEntityID());

            if (entity == null) {
                return;
            }

            entity.ticksSinceVelocity = 0;
            if (wrapper.motionY / 8000.0D > 0.1 && Math.hypot(wrapper.motionZ / 8000.0D, wrapper.motionX / 8000.0D) > 0.2) {
                entity.ticksSincePlayerVelocity = 0;
            }
        } else if (packet instanceof S08PacketPlayerPosLook) {
            mc.thePlayer.ticksSinceTeleport = 0;
        }
    };
}
