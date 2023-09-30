package com.alan.clients.module.impl.ghost.wtap;


import com.alan.clients.module.impl.ghost.WTap;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.value.Mode;

public class LegitWTap extends Mode<WTap> {

    private boolean unsprint, wTap;

    public LegitWTap(String name, WTap parent) {
        super(name, parent);
    }


    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {
        wTap = Math.random() * 100 < getParent().chance.getValue().doubleValue();

        if (!wTap) return;

        if (mc.thePlayer.isSprinting() || mc.gameSettings.keyBindSprint.isKeyDown()) {
            mc.gameSettings.keyBindSprint.setPressed(true);
            unsprint = true;
        }
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (!wTap) return;

        if (unsprint) {
            mc.gameSettings.keyBindSprint.setPressed(false);
            unsprint = false;
        }
    };
}