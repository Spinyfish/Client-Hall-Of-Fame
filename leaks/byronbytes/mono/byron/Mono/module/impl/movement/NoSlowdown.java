package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventSlow;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "NoSlowdown", description = "Swords wont slow you now.", category = Category.Movement)
public class NoSlowdown extends Module
{

    EventSlow eventSlow = new EventSlow();

    @Subscribe
    public void onSlow (EventSlow e)
    {
        e.setCancelled(true);
    }

    @Override
    public void onDisable ()
    {
        super.onDisable();
        eventSlow.setCancelled(false);

    }
    
    @Override
    public void onEnable ()
    {
        super.onEnable();
        eventSlow.setCancelled(false);

    }

}
