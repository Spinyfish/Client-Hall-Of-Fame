package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.events.EventUpdate;

public class Sprint extends Mod {

    public Sprint() {
        super("Sprint", "Sprint for you",  Category.MOVEMENT);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        mc.gameSettings.keyBindSprint.pressed = true;
    }
}
