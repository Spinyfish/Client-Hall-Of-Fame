package dev.africa.pandaware.impl.module.movement.longjump.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.potion.Potion;

public class WatchdogLongJump extends ModuleMode<LongJumpModule> {
    private boolean wasOnGround;
    private double lastDistance;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            this.lastDistance = MovementUtils.getLastDistance();
        }
        if (this.wasOnGround && mc.thePlayer.onGround) {
            parent.toggle(false);
        }
    };
    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        boolean hasSpeed = (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null);

        if (mc.thePlayer.onGround && !wasOnGround && MovementUtils.isMoving()) {
            if (hasSpeed) {
                MovementUtils.strafe(1.0);
            } else {
                MovementUtils.strafe(0.56);
            }
            mc.thePlayer.jump();
            event.y = mc.thePlayer.motionY = 0.42f;
            event.y = mc.thePlayer.motionY += PlayerUtils.getJumpBoostMotion() * 1.1;
            this.wasOnGround = true;
        }
        if (mc.thePlayer.moveStrafing != 0 && !mc.thePlayer.onGround) {
            MovementUtils.strafe(event, this.lastDistance * 0.91f);
        }

        if (mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.275 && !mc.thePlayer.isPotionActive(Potion.jump)) {
            mc.thePlayer.motionY = -0.00275f;
        }
    };

    public WatchdogLongJump(String name, LongJumpModule parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        this.wasOnGround = false;
        this.lastDistance = MovementUtils.getLastDistance();
        if (mc.getCurrentServerData() != null && !mc.getCurrentServerData().serverIP.endsWith("hypixel.net") && !(mc.currentScreen instanceof GuiMultiplayer)) {
            Client.getInstance().getNotificationManager().addNotification(Notification.Type.WARNING, "FDPClient 'developer' detected", 2);
            this.parent.toggle(false);
        }
    }
}
