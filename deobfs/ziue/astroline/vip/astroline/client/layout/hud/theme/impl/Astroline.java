/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.entity.Entity
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.hud.HUD
 *  vip.astroline.client.layout.hud.theme.Theme
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.font.FontUtils
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.WorldRenderUtils
 */
package vip.astroline.client.layout.hud.theme.impl;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.layout.hud.HUD;
import vip.astroline.client.layout.hud.theme.Theme;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.font.FontUtils;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.WorldRenderUtils;

public class Astroline
implements Theme {
    public String getName() {
        return "Astroline";
    }

    public static float getYIndex(float newWidth, float newHeight, float yIndex, String renderName) {
        float x = Theme.renderBackground((float)newWidth, (float)yIndex, (String)renderName, (int)Astroline.mc.fontRendererObj.getStringWidth(renderName), (Entity)mc.getRenderViewEntity());
        int color2 = ColorUtils.interpolateColorsBackAndForth((int)Hud.arrayListSpeed.getValue().intValue(), (int)((int)(Hud.arrayListOffset.getValue().floatValue() * yIndex)), (Color)Hud.hudColor1.getColor(), (Color)Hud.hudColor2.getColor(), (boolean)false).getRGB();
        FontUtils font = FontManager.normal2;
        if (Hud.colorbar.getValue().booleanValue()) {
            Gui.drawRect((double)((double)WorldRenderUtils.getScaledResolution().getScaledWidth() - 1.5 - (double)Hud.arrayListX.getValue().floatValue()), (double)(yIndex + 1.0f + Hud.arrayListY.getValue().floatValue()), (double)((float)WorldRenderUtils.getScaledResolution().getScaledWidth() - Hud.arrayListX.getValue().floatValue()), (double)(yIndex + 13.0f + Hud.arrayListY.getValue().floatValue()), (int)color2);
        }
        String moduleName = Hud.arraylistLowercase.getValue() != false ? renderName.toLowerCase() : renderName;
        font.drawStringWithShadow(moduleName.toLowerCase(), x - Hud.arrayListX.getValue().floatValue(), yIndex + 3.0f + Hud.arrayListY.getValue().floatValue(), color2);
        return yIndex += 12.0f;
    }

    public void render(float newWidth, float newHeight) {
        GL11.glPushMatrix();
        FontUtils font = FontManager.normal2;
        float yIndex = 2.0f + HUD.animationY;
        if (Hud.arraylist.getValue().booleanValue()) {
            for (Module module : vip.astroline.client.Astroline.INSTANCE.moduleManager.getModulesRender((Object)font)) {
                String renderName = module.getName();
                String renderSuffix = module.getDisplayName();
                yIndex = Astroline.getYIndex(newWidth, newHeight, yIndex, renderSuffix);
            }
        }
        GL11.glPopMatrix();
    }

    public void renderWatermark() {
    }
}
