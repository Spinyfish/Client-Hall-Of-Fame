package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketNoFall extends ModuleMode<NoFallModule> {
    public PacketNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.getParent().canFall() && mc.thePlayer.fallDistance > 2.5) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(true));
            mc.thePlayer.fallDistance = 0;
        }
    };
}
