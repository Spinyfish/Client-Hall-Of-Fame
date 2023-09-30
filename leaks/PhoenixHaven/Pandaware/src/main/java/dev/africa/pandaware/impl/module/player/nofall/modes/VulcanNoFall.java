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

public class VulcanNoFall extends ModuleMode<NoFallModule> {
    private boolean fixed;
    private int count;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        FlightModule fly = Client.getInstance().getModuleManager().getByClass(FlightModule.class);
        if (fly.getData().isEnabled()) return;
        if (event.getEventState() == Event.EventState.PRE) {
            if (mc.thePlayer.onGround && this.fixed) {
                this.fixed = false;
                this.count = 0;
                mc.timer.timerSpeed = 1f;
            }

            if (mc.thePlayer.fallDistance > 2f) {
                this.fixed = true;
                mc.timer.timerSpeed = 0.92f;
            }

            double amount = this.count > 2 ? 3.5f : 2.8f;

            if (mc.thePlayer.fallDistance > amount) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));

                mc.thePlayer.motionY = -0.1;
                mc.thePlayer.fallDistance = 0;
                mc.thePlayer.motionY *= 1.1f;

                if (this.count++ > 5) {
                    this.count = 0;
                }
            }
        }
    };

    public VulcanNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }
}
