package me.nekoWare.client.module.render;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;

import java.util.function.Consumer;

public class Animations extends Module {

    public static int anim;

    public Animations() {
        super("Animations", 0, Category.RENDER);
        addModes("1.7", "Sigma");
    }

    @Handler
    public Consumer<UpdateEvent> onEvent = (event) -> {
        if (isMode("1.7"))
            this.anim = 1;

        if (isMode("Sigma"))
            this.anim = 6;
    };
}
