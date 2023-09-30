package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.player.movement.LoseSprintEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;

public final class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint", Category.COMBAT, "Stops sprint reset after hitting");
    }

    @Override
    public void onKeepSprintEvent(LoseSprintEvent event) {
        event.cancel();
    }

}
