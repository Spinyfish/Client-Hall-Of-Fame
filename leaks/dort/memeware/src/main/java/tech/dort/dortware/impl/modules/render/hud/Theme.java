package tech.dort.dortware.impl.modules.render.hud;

import net.minecraft.client.Minecraft;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.impl.events.RenderHUDEvent;

public abstract class Theme {

    protected static final Minecraft mc = Minecraft.getMinecraft();
    private final Module module;

    public Theme(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public abstract void render(RenderHUDEvent event);
}
