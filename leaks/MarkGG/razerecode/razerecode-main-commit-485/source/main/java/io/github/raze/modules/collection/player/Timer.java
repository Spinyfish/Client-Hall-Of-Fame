package io.github.raze.modules.collection.player;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.NumberSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Timer extends BaseModule {

    public NumberSetting timer;

    public Timer() {
        super("Timer", "Increases the minecraft timer.", ModuleCategory.PLAYER);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                timer = new NumberSetting(this, "Timer Speed", 0.1, 50, 1)
        );
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
                mc.timer.timerSpeed = (float) timer.get().doubleValue();
        }
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1;
    }

}
