package dev.thread.api.module;

import dev.thread.api.event.bus.annotation.Subscribe;
import dev.thread.impl.event.KeyPressEvent;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleManager extends HashMap<Class<? extends Module>, Module> {
    public ModuleManager() {
        Reflections reflections = new Reflections("dev.thread.impl.module");

        try {
            for (Class<? extends Module> clazz : reflections.getSubTypesOf(Module.class)){
                put(clazz, clazz.getDeclaredConstructor().newInstance());
                System.out.println("Registered module " + get(clazz).getName());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

@Subscribe
    private void onKeyPressEvent(KeyPressEvent keyPressEvent) {
        values().forEach(m -> {
            if (keyPressEvent.getKey() == m.getKey().getValue().getKey()) {
                m.toggle();
            }
        });
    }
}
