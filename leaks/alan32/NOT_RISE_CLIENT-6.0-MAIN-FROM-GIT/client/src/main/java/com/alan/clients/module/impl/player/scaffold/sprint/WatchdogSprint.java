package com.alan.clients.module.impl.player.scaffold.sprint;

import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;

public class WatchdogSprint extends Mode<Scaffold> {
    private int blocks;
    private boolean stopped;

    public WatchdogSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        if (this.blocks <= 0) {
            this.stopped = false;
            this.blocks = 0;
            mc.gameSettings.keyBindSprint.setPressed(true);
            mc.thePlayer.setSprinting(true);
        } else {
            this.stopped = true;
        }
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mc.thePlayer.onGround) {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.thePlayer.motionX *= 1.01 - Math.random() / 100f;
                mc.thePlayer.motionZ *= 1.01 - Math.random() / 100f;
            } else {
                mc.thePlayer.setSprinting(false);
                mc.gameSettings.keyBindSprint.setPressed(false);
            }
        } else {
            mc.thePlayer.setSprinting(false);
            mc.gameSettings.keyBindSprint.setPressed(false);
        }

        if (!mc.thePlayer.isPotionActive(Potion.moveSpeed) && MoveUtil.speed() > 0.113 && mc.thePlayer.onGround) {
            MoveUtil.strafe(0.113 - Math.random() / 100f);
        }

        if (!mc.thePlayer.isSprinting()) this.stopped = true;
    };

    @EventLink
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        Packet<?> p = event.getPacket();

        if (p instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) p;
            if (wrapper.getPlacedBlockDirection() != 255) {
                if (mc.thePlayer.isSprinting()) {
                    this.blocks++;
                } else {
                    this.blocks -= 2;
                }
            }
        }
    };
}
