package alos.stella.module.modules.world;

import alos.stella.event.EventTarget;
import alos.stella.event.events.UpdateEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.value.FloatValue;

@ModuleInfo(name = "GameSpeed", description = "Make ur game speed faster", category = ModuleCategory.WORLD)
public class GameSpeed extends Module {
    public static FloatValue speed = new FloatValue("Speed", 2, 0.1f, 5);
    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.timer.timerSpeed = speed.get();
    }
    public void onDisable(){
        mc.timer.timerSpeed = 1.0f;
    }
    @Override
    public String getTag() {
        return speed.get().toString();
    }
}
