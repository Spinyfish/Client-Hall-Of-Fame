package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.KeepSprintEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;

public final class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint", Category.COMBAT, "Stops sprint reset after hitting");
    }

    @Link
    public Listener<KeepSprintEvent> onKeepSprint = e -> {
        e.setCancelled(true);
    };

}
