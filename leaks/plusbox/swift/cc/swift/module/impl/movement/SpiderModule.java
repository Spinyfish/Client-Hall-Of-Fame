package cc.swift.module.impl.movement;

import cc.swift.events.EventState;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

public class SpiderModule extends Module {
    public SpiderModule() {
        super("Spider", Category.MOVEMENT);
        registerValues(mode);
    }
    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE) {
            switch (mode.getValue()) {
                case VANILLA:
                    if (mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.motionY = 0.7;
                    }
                    break;
                case VULCAN:
                    if (mc.thePlayer.isCollidedHorizontally) {
                        event.setOnGround(true);
                        double[] pos = MovementUtil.yawPos(MovementUtil.getDirection(), -0.15F);

                        event.setX(event.getX() + pos[0]);
                        event.setZ(event.getZ() + pos[1]);
                        mc.thePlayer.motionY = 0.56 - Math.random() * 0.05;

                    }
                    break;
                case SPARKY:
                    if (mc.thePlayer.isCollidedHorizontally) {
                        event.setOnGround(true);
                        mc.thePlayer.motionY = 0.95;
                    }
            }
        }
    };
    public enum Mode{
        VANILLA, VULCAN, SPARKY
    }
}
