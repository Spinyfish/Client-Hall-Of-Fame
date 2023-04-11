package byron.Mono.module.impl.player;

import byron.Mono.event.Event;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "FastPlace", description = "Place blocks faster!", category = Category.Player)
public class FastPlace extends Module {

    @Override
    public void onEnable() {
        super.onEnable();
        mc.rightClickDelayTimer = 0;
    }

    @Subscribe
    public void onUpdate(EventUpdate e)
    {
        mc.rightClickDelayTimer = 0;
    }
    @Override
    public void onDisable() {
        super.onDisable();
        mc.rightClickDelayTimer = 6;
    }


}
