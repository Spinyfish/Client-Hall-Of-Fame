package me.jinthium.straight.impl.components;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.event.network.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class EntityTickComponent extends Component {

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING){
            if (mc == null || mc.theWorld == null) return;

            Packet<?> packet = event.getPacket();

            if (packet instanceof S12PacketEntityVelocity) {
                final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

                Entity entity = mc.theWorld.getEntityByID(wrapper.getEntityID());

                if (entity == null) {
                    return;
                }

                entity.ticksSinceVelocity = 0;
                if (wrapper.getMotionY() / 8000.0D > 0.1 && Math.hypot(wrapper.getMotionZ() / 8000.0D, wrapper.getMotionX() / 8000.0D) > 0.2) {
                    entity.ticksSincePlayerVelocity = 0;
                }
            } else if (packet instanceof S08PacketPlayerPosLook) {
                mc.thePlayer.ticksSinceTeleport = 0;
            }
        }
    };
}
