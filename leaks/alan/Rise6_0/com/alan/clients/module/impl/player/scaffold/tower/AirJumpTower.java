package com.alan.clients.module.impl.player.scaffold.tower;

import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.util.player.PlayerUtil;

public class AirJumpTower extends Mode<Scaffold> {

    public AirJumpTower(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.ticksExisted % 2 == 0 && PlayerUtil.blockNear(2)) {
            mc.thePlayer.motionY = 0.42F;
            event.setOnGround(true);
        }
    };
}
