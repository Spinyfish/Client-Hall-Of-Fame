/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiIngame
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.hud.theme.Theme
 *  vip.astroline.client.layout.hud.theme.impl.Astroline
 *  vip.astroline.client.layout.hud.theme.impl.Fade
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.hud.theme.Theme;
import vip.astroline.client.layout.hud.theme.impl.Fade;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class HUD
extends GuiIngame {
    public static float animationY = 0.0f;
    public static final List<Theme> themes = new ArrayList<Theme>();

    public HUD(Minecraft mcIn) {
        super(mcIn);
        themes.add((Theme)new Fade());
        themes.add((Theme)new vip.astroline.client.layout.hud.theme.impl.Astroline());
    }

    public void renderGameOverlay(float partialTicks) {
        super.renderGameOverlay(partialTicks);
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        float scale = 2.0f;
        int color = ColorUtils.fadeBetween((int)Hud.hudColor1.getColorInt(), (int)Hud.hudColor2.getColorInt());
        HUD.drawTextLogo(Astroline.INSTANCE.getCLIENT().toUpperCase(), 3, 3);
        FontManager.baloo16.drawString(Astroline.INSTANCE.getVERSION(), 5.0f + FontManager.vision30.getStringWidth(Astroline.INSTANCE.getCLIENT().toUpperCase()), 6.0f, color);
        if (Hud.effect.getValue().booleanValue()) {
            int x = 4;
            int y = (int)((float)(height / 2) - FontManager.wqy18.FONT_HEIGHT);
            if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
                for (PotionEffect o : this.mc.thePlayer.getActivePotionEffects()) {
                    Potion potion = Potion.potionTypes[o.getPotionID()];
                    String potionName = I18n.format((String)potion.getName(), (Object[])new Object[0]);
                    String amplifierName = o.getAmplifier() == 0 ? "" : (o.getAmplifier() == 1 ? " " + I18n.format((String)"enchantment.level.2", (Object[])new Object[0]) : (o.getAmplifier() == 2 ? " " + I18n.format((String)"enchantment.level.3", (Object[])new Object[0]) : (o.getAmplifier() == 3 ? " " + I18n.format((String)"enchantment.level.4", (Object[])new Object[0]) : " " + o.getAmplifier())));
                    String text = potionName + amplifierName;
                    String duration = Potion.getDurationString((PotionEffect)o);
                    GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    this.mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);
                    if (potion.hasStatusIcon()) {
                        int i1 = potion.getStatusIconIndex();
                        this.drawTexturedModalRect(x + 3, y, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                    }
                    RenderUtil.drawRoundedRect((float)x, (float)(y - 5), (float)((float)x + FontManager.wqy18.getStringWidth(text) + 40.0f), (float)(y + 23), (float)0.3f, (int)new Color(0, 0, 0, 40).getRGB());
                    HUD.drawText(text, x + 25, y - 2);
                    HUD.drawText(duration, x + 25, y + 8);
                    y = (int)((float)y - (FontManager.wqy18.FONT_HEIGHT + 28.0f));
                }
            }
        }
        String themeValue = Hud.theme.getValue();
        Iterator<Theme> iterator = themes.iterator();
        while (iterator.hasNext()) {
            Theme theme = iterator.next();
            if (!theme.getName().equalsIgnoreCase(themeValue)) continue;
            theme.renderWatermark();
            theme.render((float)width, (float)height);
        }
    }

    public static void drawTextLogo(String text, int x, int y) {
        int tempx = x;
        int i = 0;
        while (i < text.length()) {
            String s = String.valueOf(text.charAt(i));
            FontManager.vision30.drawStringWithShadow(s, (float)tempx, (float)y, ColorUtils.interpolateColorsBackAndForth((int)5, (int)((int)(Hud.watermarkIndexSpeed.getValue().floatValue() * (float)i)), (Color)Hud.hudColor1.getColor(), (Color)Hud.hudColor2.getColor(), (boolean)false).getRGB());
            tempx = (int)((float)tempx + FontManager.vision30.getStringWidth(s));
            ++i;
        }
    }

    public static void drawText(String text, int x, int y) {
        int tempx = x;
        int i = 0;
        while (i < text.length()) {
            String s = String.valueOf(text.charAt(i));
            FontManager.wqy15.drawString(s, (float)tempx, (float)y, ColorUtils.interpolateColorsBackAndForth((int)5, (int)((int)(Hud.watermarkIndexSpeed.getValue().floatValue() * (float)i)), (Color)Hud.hudColor1.getColor(), (Color)Hud.hudColor2.getColor(), (boolean)false).getRGB());
            tempx = (int)((float)tempx + FontManager.wqy15.getStringWidth(s));
            ++i;
        }
    }
}
