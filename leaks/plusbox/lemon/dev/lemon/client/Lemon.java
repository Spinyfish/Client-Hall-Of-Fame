package dev.lemon.client;

import dev.lemon.api.event.bus.EventBus;
import dev.lemon.api.module.ModuleManager;
import lombok.Getter;

@Getter
public enum Lemon {
    INSTANCE;

    private EventBus bus;
    private ModuleManager moduleManager;

    public void init() {
        bus = new EventBus();
        moduleManager = new ModuleManager();
        moduleManager.init();
    }
}