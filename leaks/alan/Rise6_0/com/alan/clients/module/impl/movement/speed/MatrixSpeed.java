package com.alan.clients.module.impl.movement.speed;

import com.alan.clients.module.impl.movement.Speed;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PostMotionEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.SubMode;

/**
 * @author Hazsi
 * @since 10/11/2022
 */
public class MatrixSpeed extends Mode<Speed> {

    private final ModeValue mode = new ModeValue("Sub-Mode", this)
            .add(new SubMode("Hop 1"))
            .add(new SubMode("Hop 2"))
            .add(new SubMode("Lowhop"))
            .setDefault("Hop");

    // Used to track if the player was moving on the last tick
    // Used to slow down when releasing keys
    private boolean lastTickMoving = false;

    public MatrixSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (MoveUtil.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            }

            if (mc.thePlayer.motionY > 0) {
                mc.thePlayer.motionZ *= 1.004;
                mc.thePlayer.motionX *= 1.004;
            }
        } else if (lastTickMoving) {
            mc.thePlayer.motionX /= 4;
            mc.thePlayer.motionZ /= 4;
        }

        mc.thePlayer.omniSprint = true;
    };

    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {

        lastTickMoving = MoveUtil.isMoving();
    };
}