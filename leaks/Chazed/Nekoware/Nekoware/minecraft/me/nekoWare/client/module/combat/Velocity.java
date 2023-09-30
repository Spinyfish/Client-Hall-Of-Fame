
package me.nekoWare.client.module.combat;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.PacketInEvent;
import me.nekoWare.client.module.Module;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.function.Consumer;

public class Velocity extends Module {
    public Velocity(final String name, final int key, final Module.Category category) {
        super(name, key, category);
        this.addModes("Packet");
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Handler
    public Consumer<PacketInEvent> update = e -> {
    	
    	if(this.isMode("Packet")) {
        if (mc.thePlayer == null) {
            return;
        }
        if (e.getPacket() instanceof S27PacketExplosion) {
            e.cancel();
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity packetEntityVelocity = (S12PacketEntityVelocity) e.getPacket();
            if (packetEntityVelocity.func_149412_c() != mc.thePlayer.getEntityId()) {
                return;
            }
            e.cancel();
        	}
        }
    };
}
