package dev.tenacity.module.impl.movement.longs.impl;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.longs.LongJumpMode;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class WatchdogLongJump extends LongJumpMode {

    public WatchdogLongJump() {
        super("Watchdog");
    }

    @Override
    public void onEnable() {
        ChatUtil.print("Throw a fireball on the ground.");
        super.onEnable();
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        // This is a private exploit. I don't want people to skid this.
        super.onMotionEvent(e);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) e.getPacket();
            velocityPacket.motionX = velocityPacket.motionX * 7;
            velocityPacket.motionZ = velocityPacket.motionZ * 7;
        }

        super.onPacketReceiveEvent(e);
    }

}
