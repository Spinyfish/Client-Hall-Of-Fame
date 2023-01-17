package com.alan.clients.module.impl.combat.criticals;

import com.alan.clients.module.impl.combat.Criticals;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import util.time.StopWatch;

public final class EditCriticals extends Mode<Criticals> {

    private final NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 50);

    private final double[] VALUES = new double[]{0.0005D, 0.0001D};
    private final StopWatch stopwatch = new StopWatch();

    private boolean attacked;
    private int ticks;

    public EditCriticals(String name, Criticals parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.onGround && attacked) {
            ticks++;

            switch (ticks) {
                case 1: {
                    event.setPosY(event.getPosY() + VALUES[0]);
                    break;
                }

                case 2: {
                    event.setPosY(event.getPosY() + VALUES[1]);
                    attacked = false;
                    break;
                }
            }

            event.setOnGround(false);
        } else {
            attacked = false;
            ticks = 0;
        }
    };

    @EventLink()
    public final Listener<AttackEvent> onAttackEvent = event -> {
        if (mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && stopwatch.finished(delay.getValue().longValue())) {
            mc.thePlayer.onCriticalHit(event.getTarget());

            stopwatch.reset();
            attacked = true;
        }
    };
}
