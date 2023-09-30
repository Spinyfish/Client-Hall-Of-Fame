package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.events.EventMotion;

public class LongJump extends Mod {

    public boolean hasDamaged;
    public int ticksToWait;

    public LongJump() {
        super("LongJump","Like the name implies it makes your jumps looooooooooong", Category.MOVEMENT);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {

    }
}
