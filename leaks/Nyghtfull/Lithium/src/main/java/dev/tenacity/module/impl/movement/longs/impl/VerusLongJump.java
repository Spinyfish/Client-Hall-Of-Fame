package dev.tenacity.module.impl.movement.longs.impl;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.module.impl.movement.longs.LongJumpMode;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class VerusLongJump extends LongJumpMode {

    public VerusLongJump() {
        super("Verus");
    }

    @Override
    public void onEnable() {
        ChatUtil.print("Throw a fireball on the ground.");
        super.onEnable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) e.getPacket();

            velocityPacket.motionX = velocityPacket.motionX * 2;
            velocityPacket.motionY = velocityPacket.motionY * 7;
            velocityPacket.motionZ = velocityPacket.motionZ * 2;
        }

        super.onPacketReceiveEvent(e);
    }
}