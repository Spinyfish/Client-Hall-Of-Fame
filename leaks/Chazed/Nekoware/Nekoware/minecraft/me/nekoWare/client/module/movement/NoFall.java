
package me.nekoWare.client.module.movement;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;
import me.nekoWare.client.util.packet.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.function.Consumer;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", 0, Module.Category.MISC);
        this.addModes("Spoof", "Packet");
    }
    @Handler
    public Consumer<UpdateEvent> eventConsumer0 = (e) -> {

        if (isMode("Packet")) {
            if (mc.thePlayer.fallDistance >= 2F) {
                PacketUtil.sendPacketSilent(new C03PacketPlayer(true));
                mc.thePlayer.fallDistance = 0;
            }
        }
        if(isMode("Spoof")) {
        	if (mc.thePlayer.fallDistance > 2.5F) {
        		e.setGround(true);
        	}
        }
        
    };
}
