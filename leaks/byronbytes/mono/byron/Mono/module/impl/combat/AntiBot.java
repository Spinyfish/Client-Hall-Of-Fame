package byron.Mono.module.impl.combat;

import byron.Mono.event.Event;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;


@ModuleInterface(name = "AntiBot", description = "hide anticheat bots", category = Category.Combat)
public class AntiBot extends Module {

    @Subscribe
    public void onUpdate(EventUpdate e)
    {

    }


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
