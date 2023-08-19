package cc.swift.module.impl.render.hud.impl.data;

import cc.swift.module.impl.render.hud.HudComponent;
import cc.swift.module.impl.render.hud.HudFont;
import net.minecraft.client.gui.ScaledResolution;

public abstract class DataComponent {
    public float width;
    public float lines = 1;
    public boolean enabled = true;

    public abstract void render(HudFont font, float posX, float posY);
}
