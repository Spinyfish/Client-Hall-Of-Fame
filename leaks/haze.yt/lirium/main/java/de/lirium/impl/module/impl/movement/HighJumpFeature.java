package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.ServerUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;

@ModuleFeature.Info(name = "High Jump", description = "Jump very high", category = ModuleFeature.Category.MOVEMENT)
public class HighJumpFeature extends ModuleFeature {

    private double moveSpeed;
    private int jumps;

    @EventHandler
    public final Listener<UpdateEvent> eventUpdateListener = e -> {

        if (jumps == 0) {
            if (!ServerUtil.isCubeCraft()) {
                for (int i = 0; i < 420; i++) {
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 0.008D, getZ(), false));
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                }
            } else {
                sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 3.001D, getZ(), false));
                sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
            }
            sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
            for (int i = 0; i < 2; i++) getPlayer().jump();
            getPlayer().motionY = 5.0;

            final double speed = 0.49D;
            setPosition(getX() + -Math.sin(Math.toRadians(getYaw())) * speed, getY(), getZ() + Math.cos(Math.toRadians(getYaw())) * speed);

            jumps = 1;
        }

        if (jumps == 1 && getPlayer().hurtTime > 0) {
            setSpeed(0.6);
            jumps = 2;
        }

        if (mc.player.onGround)
            setEnabled(false);
    };

    @Override
    public void onEnable() {
        super.onEnable();

        moveSpeed = 0;
        jumps = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        setTimer(1.0F);
    }
}