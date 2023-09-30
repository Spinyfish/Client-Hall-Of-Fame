package me.nekoWare.client.module.combat;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;

import java.util.function.Consumer;

public class Reach extends Module {

    public Reach() {
        super("Reach", 0, Category.COMBAT);
    }

    @Handler
    public Consumer<UpdateEvent> eventConsumer0 = (event) -> {
        mc.rightClickDelayTimer = 0;
    };
}



