package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.utilities.collection.math.TimeUtil;

public class Glide extends BaseModule {

    public ArraySetting mode;
    private final TimeUtil timer;

    public Glide() {
        super("Glide", "Makes you glide when falling", ModuleCategory.MOVEMENT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                mode = new ArraySetting(this, "Mode", "Vanilla", "Vanilla", "Vulcan", "SkyCave")
        );
        timer = new TimeUtil();
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            switch(mode.get()) {
                case "Vanilla":
                    if (mc.thePlayer.fallDistance > 1.1)
                        mc.thePlayer.motionY = -0.07F;
                    break;

                case "Vulcan":
                    if(timer.elapsed(100, true))
                        mc.thePlayer.motionY = -0.155F;
                    else
                        mc.thePlayer.motionY = -0.1F;
                    break;

                case "SkyCave":
                    if(timer.elapsed(100, true))
                        mc.thePlayer.motionY = -0.145F;
                    else
                        mc.thePlayer.motionY = -0.1F;
                    break;
            }
        }
    }
}
