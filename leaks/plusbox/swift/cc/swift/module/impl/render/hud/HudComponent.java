package cc.swift.module.impl.render.hud;

import cc.swift.Swift;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.module.Module;
import cc.swift.module.impl.render.HudModule;
import cc.swift.util.IMinecraft;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@Setter
public abstract class HudComponent implements IMinecraft {

    protected float posX, posY, width, height;
    private boolean dragging;
    private int lines;

    private boolean isEnabled;

    public HudComponent(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    protected final HudModule getParentModule() {
        return Swift.INSTANCE.getModuleManager().getModule(HudModule.class);
    }

    public abstract void render(HudFont font);
}
