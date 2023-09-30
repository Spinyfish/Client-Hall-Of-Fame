package Reality.Realii.mods.modules.world;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Timer extends Module {
    public Numbers<Number> timer = new Numbers<>("Timer", 1.2, 0, 5, 0.1);
    float set;

    public Timer() {
        super("Timer", ModuleType.World);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.timer.timerSpeed = timer.getValue().floatValue();
        set = mc.timer.timerSpeed;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.timer.timerSpeed == set) {
            mc.timer.timerSpeed = 1;
        }
    }

    @EventHandler
    public void onUpdate() {

    }
}
