package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.SafeWalkEvent;

@ModuleInfo(name = "SafeWalk", category = Category.MOVEMENT)
public class SafeWalkModule extends Module {

    @EventHandler
    EventCallback<SafeWalkEvent> onEvent = Event::cancel;
}