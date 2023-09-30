package Reality.Realii.mods.modules.misc;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class IRC extends Module {
    public IRC(){
        super("IRC", ModuleType.Misc);
    }
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventHandler
    public void onUpdate(EventPostUpdate e) {

    }
}
