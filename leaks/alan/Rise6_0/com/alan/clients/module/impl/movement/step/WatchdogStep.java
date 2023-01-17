package com.alan.clients.module.impl.movement.step;

import com.alan.clients.module.impl.movement.Step;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.StepEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Auth
 * @since 22/3/2022
 */

public class WatchdogStep extends Mode<Step> {

    private final NumberValue height = new NumberValue("Height", this, 1, 1, 1.5, 0.1);
    private final NumberValue timer = new NumberValue("Timer", this, 0.5, 0.1, 1, 0.1);

    public WatchdogStep(String name, Step parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.onGround && !PlayerUtil.inLiquid()) {
            mc.thePlayer.stepHeight = this.height.getValue().floatValue();
        } else {
            mc.thePlayer.stepHeight = 0.6F;
        }
    };

    @EventLink()
    public final Listener<StepEvent> onStep = event -> {
        if (!mc.thePlayer.onGround || PlayerUtil.inLiquid()) {
            return;
        }

        final double height = event.getHeight();

        if (height <= 0.6F) {
            return;
        }

        final double[] values;

        if (height > 1.015) {
            values = new double[]{0.42F, 0.7532F, 1.0F, 0.98F};
        } else if (height > 0.875) {
            values = new double[]{0.42F, 0.7532F, 1.0F};
        } else {
            values = new double[]{0.39F, 0.6938F};
        }

        mc.timer.timerSpeed = this.timer.getValue().floatValue();

        for (final double d : values) {
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
        }
    };

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.6F;
    }
}