package com.alan.clients.module.impl.movement.step;

import com.alan.clients.module.impl.movement.Step;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.StepEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Alan
 * @since 22/3/2022
 */

public class MatrixStep extends Mode<Step> {

    private final BooleanValue twoBlockValue = new BooleanValue("2 Block", this, true);
    private final BooleanValue instantValue = new BooleanValue("Instant", this, true, twoBlockValue::getValue);

    private int ticks;
    private boolean doJump;

    public MatrixStep(String name, Step parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.6F;
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        mc.thePlayer.stepHeight = twoBlockValue.getValue() ? 2 : 1;
        if (doJump) {
            if ((ticks > 0 && mc.thePlayer.onGround) || ticks > 5) {
                ticks = 0;
                doJump = false;
                return;
            }
            if (ticks % 3 == 0) {
                event.setOnGround(true);
                mc.thePlayer.jump();
            }
            ticks++;
        }
    };

    @EventLink()
    public final Listener<StepEvent> onStep = event -> {
        if (event.getHeight() > 1.0F) {
            if (instantValue.getValue()) {
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698, mc.thePlayer.posZ, false));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212, mc.thePlayer.posZ, false));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.00133597911215, mc.thePlayer.posZ, true));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.42133596599913, mc.thePlayer.posZ, false));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.75453595963335, mc.thePlayer.posZ, false));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 2.0026719582243, mc.thePlayer.posZ, false));
                mc.timer.timerSpeed = 1.0F / 7F;
            } else {
                doJump = true;
                ticks = 0;
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }
            return;
        }
        if (event.getHeight() > 0.6F) {
            mc.timer.timerSpeed = 0.33333f;
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F, mc.thePlayer.posZ, false));
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F, mc.thePlayer.posZ, true));
        }
    };
}