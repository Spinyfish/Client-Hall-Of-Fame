package com.alan.clients.module.impl.player.scaffold.sprint;

import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.JumpEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class WatchdogSprint extends Mode<Scaffold>{

    private int block, slow;

    public WatchdogSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
    }

    @EventLink(value = Priorities.HIGH)
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        mc.thePlayer.setSprinting(false);

        block++;

        if (block < 10 && mc.thePlayer.onGround) {
            if (MoveUtil.isMoving() && slow <= 4 && !mc.thePlayer.isCollidedHorizontally) {
                mc.gameSettings.keyBindSprint.setPressed(true);
                mc.thePlayer.setSprinting(true);
                final double[] xz = yawPos((float) ((float) MoveUtil.direction() - (Math.random() / 100f)), ((MoveUtil.getAllowedHorizontalDistance() - Math.random() / 100f) / (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 2 : 2)));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - xz[0], mc.thePlayer.posY, mc.thePlayer.posZ - xz[1], false));
                slow--;
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.thePlayer.motionX *= 0.8 - Math.random() / 1000f;
                mc.thePlayer.motionZ *= 0.8 - Math.random() / 1000f;
            } else {
                mc.thePlayer.motionX *= 0.945 - Math.random() / 1000f;
                mc.thePlayer.motionZ *= 0.945 - Math.random() / 1000f;
            }
        }
    };

    private double[] yawPos(float yaw, double value) {
        return new double[]{-MathHelper.sin(yaw) * value, MathHelper.cos(yaw) * value};
    }

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        final Packet<?> p = event.getPacket();

        if (p instanceof C08PacketPlayerBlockPlacement) {
            final C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) p;

            if (wrapper.getPlacedBlockDirection() != 255) {
                block = 0;
                slow = 3;
            }
        }
    };

    @EventLink()
    public final Listener<JumpEvent> onJump = event -> {

        event.setCancelled(true);
        mc.thePlayer.motionY = MoveUtil.jumpMotion();
    };
}
