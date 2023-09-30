package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class MineboxFlight extends ModuleMode<FlightModule> {
    public MineboxFlight(String name, FlightModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                event.cancel();
            }
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.getParent().toggle(false);
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        event.y = mc.thePlayer.motionY = 0;
        MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed() * 11);
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> event.setOnGround(false);

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 0.1f;

        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.42f;
        }

        if (!ServerUtils.isOnServer("juega.minebox.es") && !ServerUtils.isOnServer("mc.minebox.es")) {
            parent.toggle(false);

            Client.getInstance().getNotificationManager().addNotification(Notification.Type.NOTIFY, "Minebox only", 1);
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        MovementUtils.strafe(0);
    }
}
