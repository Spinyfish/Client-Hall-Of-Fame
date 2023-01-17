package com.alan.clients.module.impl.player.antivoid;


import com.alan.clients.module.impl.player.AntiVoid;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.player.PlayerUtil;

public class PositionAntiVoid extends Mode<AntiVoid>  {

    private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

    public PositionAntiVoid(String name, AntiVoid parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.fallDistance > distance.getValue().floatValue() && !PlayerUtil.isBlockUnder()) {
            event.setPosY(event.getPosY() + mc.thePlayer.fallDistance);
        }
    };
}