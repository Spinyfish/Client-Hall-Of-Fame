package io.github.raze.modules.collection.player;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.NumberSetting;

import java.awt.*;
import java.awt.event.InputEvent;

public class FastPlace extends BaseModule {

    public ArraySetting mode;
    public NumberSetting speed;

    public FastPlace() {
        super("FastPlace", "Allows you to place blocks faster", ModuleCategory.PLAYER);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                mode = new ArraySetting(this, "Mode", "Hold", "Hold", "Auto"),
                speed = new NumberSetting(this, "Speed", 0, 10, 1)
        );
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            switch (mode.get()) {
                case "Hold": {
                    mc.rightClickDelayTimer = speed.get().intValue();
                    break;
                }
                case "Auto": {
                    if (mc.currentScreen == null) {
                        try {
                            click();
                        } catch (AWTException exception) {
                            exception.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }
    }

    private void click() throws AWTException {
        Robot bot = new Robot();
        bot.mousePress(InputEvent.BUTTON3_MASK);
        bot.mouseRelease(InputEvent.BUTTON3_MASK);
    }

    public void onDisable() {
        mc.rightClickDelayTimer = 6;
    }

}