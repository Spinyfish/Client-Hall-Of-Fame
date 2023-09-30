package dev.echo.module.impl.render;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.render.HurtCamEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam", Category.RENDER, "removes shaking after being hit");
    }

    @Link
    public Listener<HurtCamEvent> onRenderHurtCam = e -> {
        e.setCancelled(true);
    };

}
