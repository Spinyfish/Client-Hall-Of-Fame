package io.github.raze.modules.collection.combat;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.NumberSetting;
import io.github.raze.utilities.collection.math.TimeUtil;
import org.lwjgl.input.Mouse;

public class AutoClicker extends BaseModule {

    private final TimeUtil timer = new TimeUtil();
    private final NumberSetting maxCps;
    private final NumberSetting minCps;

    public AutoClicker() {
        super("AutoClicker", "Auto Clicks", ModuleCategory.COMBAT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                maxCps = new NumberSetting(this, "Max CPS", 0, 50, 17),
                minCps = new NumberSetting(this, "Min CPS", 0, 50, 12)
        );

    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            int up = 0;
            int down = 0;
            if(timer.elapsed((long) (Math.random() * 2500), true)) {
                up = (int) (Math.random() * minCps.get().intValue() / 2.4);
            }

            if(timer.elapsed((long) (Math.random() * 2700), true)) {
                down = (int) (Math.random() * maxCps.get().intValue() / 2.4);
            }

            if(Mouse.isButtonDown(0)) {
                int cps = ((int) ((((maxCps.get().intValue() - minCps.get().intValue()) * 2 + Math.random() * 3) * 1.2 / 0.98) + Math.random() * 5) + up) - down;

                if(minCps.get().intValue() > cps) {
                    cps += 2;
                }

                if(maxCps.get().intValue() < cps) {
                    cps -= 2;
                }

                if(timer.elapsed(1000 / cps, true)) {
                    mc.clickMouse();
                    timer.reset();
                }
            }
        }
    }
}
