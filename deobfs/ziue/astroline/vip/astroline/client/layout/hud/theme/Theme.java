/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.entity.Entity
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 *  vip.astroline.client.storage.utils.render.render.WorldRenderUtils
 */
package vip.astroline.client.layout.hud.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;
import vip.astroline.client.storage.utils.render.render.WorldRenderUtils;

public interface Theme {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public String getName();

    public void render(float var1, float var2);

    public void renderWatermark();

    public static float renderBackground(float newWidth, float yIndex, String renderName, int stringWidth, Entity renderViewEntity) {
        float margin = 3.0f;
        float x = newWidth - FontManager.normal2.getStringWidth(renderName) - (Hud.colorbar.getValue() != false && !Astroline.INSTANCE.moduleManager.getModule("Shaders").isToggled() ? 4.5f : 4.5f);
        int color2 = ColorUtils.interpolateColorsBackAndForth((int)Hud.arrayListSpeed.getValue().intValue(), (int)((int)(Hud.arrayListOffset.getValue().floatValue() * yIndex)), (Color)Hud.hudColor1.getColor(), (Color)Hud.hudColor2.getColor(), (boolean)false).getRGB();
        if (Hud.colorbar.getValue().booleanValue()) {
            Gui.drawRect((double)((double)WorldRenderUtils.getScaledResolution().getScaledWidth() - 1.5 - (double)Hud.arrayListX.getValue().floatValue()), (double)(yIndex + Hud.arrayListY.getValue().floatValue()), (double)((float)WorldRenderUtils.getScaledResolution().getScaledWidth() - Hud.arrayListX.getValue().floatValue()), (double)(yIndex + 10.0f + Hud.arrayListY.getValue().floatValue()), (int)color2);
        }
        if (Hud.background.getValue() == false) return x;
        RenderUtil.drawRect((float)((float)((int)(x - margin + 1.0f)) - Hud.arrayListX.getValue().floatValue()), (float)((float)((int)yIndex) + Hud.arrayListY.getValue().floatValue()), (float)(x + FontManager.normal2.getStringWidth(renderName) + margin - Hud.arrayListX.getValue().floatValue()), (float)(yIndex + 10.0f + Hud.arrayListY.getValue().floatValue()), (int)new Color(0, 0, 0, 50).getRGB());
        return x;
    }

    public static int getLineColor() {
        return -1;
    }
}
