package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;

public class Test extends Module {
    public Test() {
        super("Test", Category.COMBAT, "Testing purposes.");
    }

    @Link
    public Listener<TickEvent> tickEventListener = e -> {

    };
}