package dev.africa.pandaware.impl.ui.clickgui.setting.api;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.api.screen.GUIRenderer;
import dev.africa.pandaware.api.setting.Setting;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import lombok.Getter;

@Getter
public abstract class Element<T extends Setting> implements GUIRenderer {
    private final Module module;
    private final ModuleMode<?> moduleMode;
    private final T setting;

    private Vec2i position;
    private Vec2i size;

    public Element(Module module, ModuleMode<?> moduleMode, T setting) {
        this.module = module;
        this.moduleMode = moduleMode;
        this.setting = setting;
    }

    public void update(Vec2i position, Vec2i size) {
        this.position = position;
        this.size = size;
    }
}
