package dev.thread.client;

import dev.thread.api.event.bus.EventBus;
import dev.thread.api.module.ModuleManager;
import dev.thread.api.util.render.shader.ShaderManager;
import dev.thread.api.util.render.font.FontManager;
import lombok.Getter;

@Getter
public enum Thread {
    INSTANCE;

    private final EventBus bus;
    private final FontManager fontManager;
    private final ModuleManager moduleManager;
    private final ShaderManager shaderManager;

    Thread() {
        bus = new EventBus();
        fontManager = new FontManager();
        moduleManager = new ModuleManager();
        bus.subscribe(moduleManager);
        shaderManager = new ShaderManager();
    }
}