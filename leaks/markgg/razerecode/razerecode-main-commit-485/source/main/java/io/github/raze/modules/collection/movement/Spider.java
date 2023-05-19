package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.NumberSetting;
import io.github.raze.utilities.collection.MoveUtil;

public class Spider extends BaseModule {

    private long ticks = 0;

    public ArraySetting mode;
    public NumberSetting motion;

    public Spider() {
        super("Spider", "Allows you to Climb Walls.", ModuleCategory.MOVEMENT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Mode", "Normal", "Normal", "Motion", "Vulcan"),
                motion = new NumberSetting(this, "Motion", 0, 1, 0.8)
        );
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {

        ticks ++;
        if (!MoveUtil.isMoving())
            return;

        if (eventMotion.getState() == BaseEvent.State.PRE) {

            switch (mode.get()) {
                case "Normal": {
                    if (mc.thePlayer.isCollidedHorizontally())
                        mc.thePlayer.jump();
                    break;
                }
                case "Motion": {
                    if (mc.thePlayer.isCollidedHorizontally)
                        mc.thePlayer.motionY = this.motion.get().doubleValue();
                    break;
                }
                case "Vulcan": {
                    if (mc.thePlayer.isCollidedHorizontally()) {
                        if (ticks % 3 == 0) {
                            eventMotion.setOnGround(true);
                            mc.thePlayer.motionY = 0.5;
                        }
                    }

                    break;
                }
            }
        }

    }

}