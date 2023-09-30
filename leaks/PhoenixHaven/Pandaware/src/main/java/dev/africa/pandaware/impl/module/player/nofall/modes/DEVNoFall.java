package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class DEVNoFall extends ModuleMode<NoFallModule> {
    private boolean fixed;
    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (Client.getInstance().getModuleManager().getByClass(FlightModule.class).getData().isEnabled()) return;
        if (event.getEventState() == Event.EventState.PRE) {
            if (mc.thePlayer.fallDistance > 2f) {
                fixed = false;
                mc.timer.timerSpeed = 0.9f;
            }
            if (mc.thePlayer.fallDistance > 3.5f) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                mc.thePlayer.motionY = -0.1;
                mc.thePlayer.fallDistance = 0;
            }
            if (mc.thePlayer.onGround && !fixed) {
                fixed = true;
                mc.timer.timerSpeed = 1f;
            }
        }
    };

    public DEVNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        fixed = false;
    }
}
