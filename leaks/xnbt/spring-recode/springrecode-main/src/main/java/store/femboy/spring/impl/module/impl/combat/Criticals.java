package store.femboy.spring.impl.module.impl.combat;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;

public final class Criticals extends Module {
    ModeSetting mode = new ModeSetting("Mode", "OnGround", "OnGround");

    public Criticals() {
        super("Criticals", "Makes u crit every time", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler(EventPriority.HIGHEST)
    public final Listener<EventMotion> onUpdate = e ->{
        switch(mode.getMode()){
            case "OnGround":
                if(mc.thePlayer.onGround){

                }
        }
    };
}
