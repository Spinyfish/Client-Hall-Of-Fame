package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.List;

@ModuleInfo(name = "Velocity", category = Category.COMBAT)


public class Velocity extends Module {

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch (event.getEventState()) {
            case RECEIVING:
                if ((event.getPacket() instanceof S12PacketEntityVelocity) && (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId())) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    event.setCancelled(true);
                }
                if (event.getPacket() instanceof S27PacketExplosion) {
                    S27PacketExplosion packetExplosion = (S27PacketExplosion) event.getPacket();
                    event.setCancelled(true);
                }
                break;
        }
    };
}
