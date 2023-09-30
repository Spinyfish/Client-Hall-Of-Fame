package dev.africa.pandaware.impl.module.movement.spider;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.StepEvent;
import dev.africa.pandaware.impl.module.movement.spider.modes.*;

@ModuleInfo(name = "Spider", category = Category.MOVEMENT)
public class SpiderModule extends Module {
    public SpiderModule() {
        this.registerModes(
                new VerusSpider("Verus", this)
        );
    }

    @EventHandler
    EventCallback<StepEvent> onStep = Event::cancel;

    @Override
    public void onDisable() {
        mc.thePlayer.motionY = 0;
    }
}
