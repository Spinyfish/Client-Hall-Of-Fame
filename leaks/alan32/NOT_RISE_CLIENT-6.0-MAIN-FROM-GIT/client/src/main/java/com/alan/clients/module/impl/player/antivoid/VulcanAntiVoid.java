package com.alan.clients.module.impl.player.antivoid;

import com.alan.clients.module.impl.player.AntiVoid;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;

/**
 * @author Strikeless
 * @since 18.03.2022
 */
public class VulcanAntiVoid extends Mode<AntiVoid> {

    private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

    private boolean teleported;

    public VulcanAntiVoid(String name, AntiVoid parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.fallDistance > distance.getValue().floatValue()) {
            event.setPosY(event.getPosY() - event.getPosY() % 0.015625);
            event.setOnGround(true);

            mc.thePlayer.motionY = -0.08D;

            MoveUtil.stop();
        }

        if (teleported) {
            MoveUtil.stop();
            teleported = false;
        }
    };

    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {

        if (mc.thePlayer.fallDistance > distance.getValue().floatValue()) {
            teleported = true;
        }
    };
}
