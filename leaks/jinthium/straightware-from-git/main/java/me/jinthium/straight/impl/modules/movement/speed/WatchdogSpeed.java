package me.jinthium.straight.impl.modules.movement.speed;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

import java.util.concurrent.ConcurrentLinkedQueue;

@ModeInfo(name = "Watchdog", parent = Speed.class)
public class WatchdogSpeed extends ModuleMode<Speed> {
    private final ModeSetting hopMode = new ModeSetting("Hop Mode", "Ground Strafe", "Ground Strafe", "Full Strafe (test)");
    private final ConcurrentLinkedQueue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();
    private double moveSpeed, groundY;

    public WatchdogSpeed() {
        this.registerSettings(hopMode);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if (event.isPre() && hopMode.is("Full Strafe (test)")) {
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if (mc.thePlayer.onGround) {
                packetQueue.forEach(PacketUtil::sendPacketNoEvent);
                packetQueue.clear();
                groundY = mc.thePlayer.posY;

                mc.thePlayer.motionY = 0.42F;
                moveSpeed = 0.98;
            } else if (mc.thePlayer.posY > groundY) {
                if (killAura.target != null)
                    mc.timer.timerSpeed = 0.9f;
                else
                    mc.timer.timerSpeed = 1.0f;

                event.setPosY(groundY);
                event.setOnGround(mc.thePlayer.hurtTime == 0);
            }

            MovementUtil.strafe(MovementUtil.getBaseMoveSpeed() * moveSpeed);
            moveSpeed -= moveSpeed / 44;
        }
    };

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        if(mc.thePlayer.ticksSinceTeleport > 40)
            MovementUtil.useDiagonalSpeed(event);

        TargetStrafe.strafe(event);
        if (hopMode.getMode().equals("Ground Strafe")) {
            MovementUtil.moveFlying(Math.random() / 500);
            if (mc.thePlayer.onGround && mc.thePlayer.ticksSinceJump > 5 && mc.thePlayer.isMoving()) {
                mc.thePlayer.motionX *= Math.random() / 250;
                mc.thePlayer.motionZ *= Math.random() / 250;
                double lastAngle = Math.atan(mc.thePlayer.lastMotionX / mc.thePlayer.lastMotionZ) * (180 / Math.PI);

                event.setFriction((float) (MovementUtil.getAllowedHorizontalDistance() * 1.48));
                mc.thePlayer.jump();

                double angle = Math.atan(mc.thePlayer.motionX / mc.thePlayer.motionZ) * (180 / Math.PI);

                if (Math.abs(lastAngle - angle) > 20 && mc.thePlayer.ticksSinceVelocity > 20) {
                    int speed = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;

                    switch (speed) {
                        case 0 -> event.setFriction(event.getFriction() - 0.005f);
                        case 1 -> event.setFriction(event.getFriction() - 0.035f);
                        default -> event.setFriction(event.getFriction() - 0.04f);
                    }
                }
            }
        }
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if (event.getPacketState() == PacketEvent.PacketState.SENDING && hopMode.is("Full Strafe (test)")) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                event.cancel();
                packetQueue.add(event.getPacket());
            }
        }
    };

    @Override
    public void onEnable() {
        moveSpeed = 0;
        groundY = mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        if (hopMode.is("Full Strafe (test)")) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, groundY, mc.thePlayer.posZ);
            mc.thePlayer.motionY = 0;
        }

        if (!packetQueue.isEmpty()) {
            packetQueue.forEach(PacketUtil::sendPacketNoEvent);
            packetQueue.clear();
        }
        super.onDisable();
    }
}
