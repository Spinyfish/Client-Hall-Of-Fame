package com.alan.clients.module.impl.player.scaffold.tower;

import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;

public class MatrixTower extends Mode<Scaffold> {

    public MatrixTower(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtil.isBlockUnder(2, false) && mc.thePlayer.motionY < 0.2) {
            mc.thePlayer.motionY = 0.42F;
            event.setOnGround(true);
        }
    };
}
