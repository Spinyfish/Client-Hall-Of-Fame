package com.alan.clients.module.impl.movement.noslow;

import com.alan.clients.module.impl.movement.NoSlow;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.SlowDownEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;

/**
 * @author Alan
 * @since 18/11/2021
 */

public class VariableNoSlow extends Mode<NoSlow> {

    private final NumberValue multiplier = new NumberValue("Multiplier", this, 0.8, 0.2, 1, 0.05);

    public VariableNoSlow(String name, NoSlow parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<SlowDownEvent> onSlowDown = event -> {
        event.setForwardMultiplier(multiplier.getValue().floatValue());
        event.setStrafeMultiplier(multiplier.getValue().floatValue());
    };
}