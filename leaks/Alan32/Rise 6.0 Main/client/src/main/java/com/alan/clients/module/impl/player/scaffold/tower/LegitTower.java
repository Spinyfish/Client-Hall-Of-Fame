package com.alan.clients.module.impl.player.scaffold.tower;

import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.value.Mode;

public class LegitTower extends Mode<Scaffold> {
    // Bypasses jump delay, holding down space is slower than this
    public LegitTower(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
    };
}
