package com.alan.clients.module.impl.combat.velocity;


import com.alan.clients.module.impl.combat.Velocity;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;

public final class GroundVelocity extends Mode<Velocity>  {

    private final NumberValue delay = new NumberValue("Delay", this, 1, 0, 20, 1);

    private int ticks;

    public GroundVelocity(String name, Velocity parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;

        if (ticks == delay.getValue().intValue()) {
            mc.thePlayer.onGround = true;
        }

        ticks++;
    };

    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        if (ticks == delay.getValue().intValue() + 1) {
            event.setJump(false);
        }
    };
}
