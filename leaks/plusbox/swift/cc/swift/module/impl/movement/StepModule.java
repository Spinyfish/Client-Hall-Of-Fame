package cc.swift.module.impl.movement;

import cc.swift.events.StepEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

public class StepModule extends Module {
    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());
    public final DoubleValue height = new DoubleValue("Height", 1d, 1, 10, 0.5).setDependency(()-> mode.getValue() == Mode.VANILLA);
    private int offGroundTicks;
    private boolean shouldStep, stepped;
    public StepModule() {
        super("Step", Category.MOVEMENT);
        registerValues(mode, height);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (!MovementUtil.isOnGround())
            offGroundTicks++;
        else
            offGroundTicks = 0;

        switch (mode.getValue()) {
            case VANILLA:
                mc.thePlayer.stepHeight = height.getValue().floatValue();
                break;
            case HYPIXEL:
                if (shouldStep) {
                    mc.thePlayer.jump();
                    stepped = true;
                    shouldStep = false;
                }
                if (offGroundTicks == 3 && stepped) {
                    mc.thePlayer.motionY = -0.2;
                    MovementUtil.setSpeed(0.4);
                    stepped = false;
                }
        }
    };

    @Handler
    public final Listener<StepEvent> stepEventListener = event -> {
        switch (mode.getValue()) {
            case HYPIXEL:
                if (event.getHeight() == 0d) {
                    shouldStep = true;
                }
                break;
        }
    };

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.stepHeight = 0.6f;
        shouldStep = false;
        stepped = false;
    }

    public enum Mode {
        VANILLA, HYPIXEL
    }
}