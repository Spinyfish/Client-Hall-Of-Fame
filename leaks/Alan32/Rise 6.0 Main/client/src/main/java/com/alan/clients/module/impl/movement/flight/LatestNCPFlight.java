package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Alan
 * @since 11/06/2022
 */

public class LatestNCPFlight extends Mode<Flight> {
    private final ModeValue mode = new ModeValue("NCP Mode", this)
            .add(new SubMode("Normal"))
            .add(new SubMode("Clip"))
            .setDefault("Normal");

    private double moveSpeed;
    private boolean started, notUnder, clipped, teleport;

    public LatestNCPFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (teleport) {
            event.setCancelled(true);
            teleport = false;
            ChatUtil.display("Teleported");
        }
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        if(!mode.getValue().getName().equals("Clip")) return;

        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() || started) {
            switch (mc.thePlayer.offGroundTicks) {
                case 0:
                    if (notUnder) {
                        if (clipped) {
                            started = true;
                            event.setSpeed(10);
                            mc.thePlayer.motionY = 0.42f;
                            notUnder = false;
                        }
                    }
                    break;

                case 1:
                    if (started) event.setSpeed(9.6);
                    break;

                default:
//                    if (mc.thePlayer.fallDistance > 0 && started) {
//                        mc.thePlayer.motionY += 2.5 / 100f;
//                    }
                    break;
            }
        } else {
            notUnder = true;

            if (clipped) return;

            clipped = true;

            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));

            teleport = true;
        }

        MoveUtil.strafe();

        mc.timer.timerSpeed = 0.4f;
    };

    @EventLink()
    private final Listener<PreMotionEvent> preMotionEventListener = event -> {
        if(!mode.getValue().getName().equals("Normal")) return;

        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

        if(started) {
            mc.thePlayer.motionY += 0.025;
            MoveUtil.strafe(moveSpeed *= 0.935F);

            if(mc.thePlayer.motionY < -0.5 && !PlayerUtil.isBlockUnder()) {
                toggle();
            }
        }

        if(mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && !started) {
            started = true;
            mc.thePlayer.jump();
            MoveUtil.strafe(moveSpeed = 9);
        }
    };

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }

    @Override
    public void onEnable() {
        ChatUtil.display("Start the fly under the block and walk forward");

        moveSpeed = 0;
        notUnder = false;
        started = false;
        clipped = false;
        teleport = false;
    }
}
