package client.module.impl.movement;

import client.Client;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.SlowDownEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;

@ModuleInfo(name = "No Slow", description = "", category = Category.MOVEMENT)
public class NoSlow extends Module {
    @EventLink()
    public final Listener<SlowDownEvent> onSlowDown = event -> {
        event.setCancelled(true);
    };

}
