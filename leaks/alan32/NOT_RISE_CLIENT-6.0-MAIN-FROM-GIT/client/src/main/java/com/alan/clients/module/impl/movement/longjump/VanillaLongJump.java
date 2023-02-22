package com.alan.clients.module.impl.movement.longjump;

import com.alan.clients.module.impl.movement.LongJump;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class VanillaLongJump extends Mode<LongJump> {

    private final NumberValue height = new NumberValue("Height", this, 0.5, 0.1, 1, 0.01);
    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

    public VanillaLongJump(String name, LongJump parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        if (InstanceAccess.mc.thePlayer.onGround) {
            InstanceAccess.mc.thePlayer.motionY = height.getValue().floatValue();
        }

        event.setSpeed(speed.getValue().floatValue());
    };

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }
}