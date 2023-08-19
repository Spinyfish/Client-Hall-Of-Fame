package dev.lemon.api.module;

import dev.lemon.api.event.bus.annotation.Subscribe;
import dev.lemon.api.event.bus.listener.IListener;
import dev.lemon.client.Lemon;
import dev.lemon.impl.event.KeyPressEvent;
import dev.lemon.impl.module.movement.Sprint;

import java.util.HashMap;

public class ModuleManager extends HashMap<Class<? extends Module>, Module> {
    public void init() {
        Lemon.INSTANCE.getBus().subscribe(this);
        put(Sprint.class, new Sprint());
    }

    @Subscribe
    private final IListener<KeyPressEvent> keyPressEvent = e -> {
        values().forEach(m -> {
            if (e.getKey() == m.getKey()) {
                m.setEnabled(!m.isEnabled());
            }
        });
    };
}
