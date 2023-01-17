package com.alan.clients.module.impl.movement.speed;

import com.alan.clients.module.impl.movement.Speed;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;

/**
 * @author Alan
 * @since 18/11/2022
 */

public class WatchdogSpeed extends Mode<Speed> {

    private double lastGroundY;
    private final NumberValue timer = new NumberValue("Timer", this, 1, 1, 2, 0.1);
    private final ModeValue mode = new ModeValue("Type", this)
            .add(new SubMode("Full Strafe"))
            .add(new SubMode("Ground Strafe"))
            .setDefault("Full Strafe");

    private final BooleanValue damageBoost = new BooleanValue("Damage Boost", this, false);
    private double velocity = 0;

    public WatchdogSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        switch (mode.getValue().getName()) {
            case "Full Strafe":
                if (damageBoost.getValue()) {
                    if (mc.thePlayer.ticksSinceVelocity == 1 && velocity != 0) {
                        MoveUtil.strafe(0.5 - Math.random() / 100f);
                        mc.thePlayer.motionY = velocity;
                    }
                }

                if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    MoveUtil.strafe(MoveUtil.getAllowedHorizontalDistance() - Math.random() / 50f);
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && lastGroundY == mc.thePlayer.posY) {
                        MoveUtil.strafe(MoveUtil.speed() * 1.2);
                    }

                    lastGroundY = mc.thePlayer.posY;

                    mc.thePlayer.jump();
                }

                if (mc.thePlayer.fallDistance < 1) {
                    if (mc.thePlayer.offGroundTicks != 5) {
                        double motionX = mc.thePlayer.motionX;
                        double motionZ = mc.thePlayer.motionZ;

                        MoveUtil.strafe();

                        if (!mc.thePlayer.onGround && mc.thePlayer.ticksSinceVelocity > 6) {
                            mc.thePlayer.motionX = (motionX + mc.thePlayer.motionX * 1.5) / 2.5;
                            mc.thePlayer.motionZ = (motionZ + mc.thePlayer.motionZ * 1.5) / 2.5;
                        }
                    }
                }
                break;

            case "Ground Strafe":
                switch (mc.thePlayer.offGroundTicks) {
                    case 0:
                        MoveUtil.strafe(MoveUtil.getAllowedHorizontalDistance() - Math.random() / 1000f);
                        mc.thePlayer.jump();
                        break;
                }

//                if (mc.thePlayer.ticksSinceVelocity < 20) {
//                    MoveUtil.strafe();
//                }
                break;
        }

        mc.timer.timerSpeed = timer.getValue().floatValue();
    };


    @EventLink()
    public final Listener<PacketReceiveEvent> onReceive = event -> {
        Packet packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                if (velocity > wrapper.motionY / 8000.0D) {
                    velocity = wrapper.motionY / 8000.0D;
                    event.setCancelled(true);
                }
            }
        }
    };
}
