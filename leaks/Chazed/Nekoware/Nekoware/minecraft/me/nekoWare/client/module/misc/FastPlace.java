package me.nekoWare.client.module.misc;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;

import java.util.function.Consumer;

public class FastPlace extends Module {

    public FastPlace() {
        super("FastPlace", 0, Category.MISC);
    }

    @Handler
    public Consumer<UpdateEvent> eventConsumer0 = (event) -> {
        mc.rightClickDelayTimer = 0;
    };
}
