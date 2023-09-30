package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.utilities.collection.MoveUtil;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends BaseModule {

    public ArraySetting mode;

    public Sprint() {
        super("Sprint", "Makes you sprint always.", ModuleCategory.MOVEMENT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Mode", "Legit", "Legit", "All Directions")

        );
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {

        if (!MoveUtil.isMoving() || mc.thePlayer.isSprinting()) {
            return;
        }

        if (eventMotion.getState() == BaseEvent.State.PRE) {
            switch (mode.get()) {
                case "Legit": {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                    break;
                }
                case "All Directions": {
                    mc.thePlayer.setSprinting(true);
                    break;
                }
            }
        }
    }
}
