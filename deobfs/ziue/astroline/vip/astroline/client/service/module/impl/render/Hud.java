/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  vip.astroline.client.service.event.impl.render.EventShader
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.ColorValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 *  vip.astroline.client.storage.utils.render.render.WorldRenderUtils
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vip.astroline.client.service.event.impl.render.EventShader;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.ColorValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.storage.utils.render.render.RenderUtil;
import vip.astroline.client.storage.utils.render.render.WorldRenderUtils;

public class Hud
extends Module {
    public static ModeValue theme = new ModeValue("Hud", "HUD Theme", "Fade", new String[]{"Rainbow", "Rainbow2", "Color", "Fade", "Gradient", "Nostalgia", "Astroline"});
    public static ColorValue hudColor1 = new ColorValue("HUD", "Hud Color 1", new Color(0xFF0000));
    public static ColorValue hudColor2 = new ColorValue("HUD", "Hud Color 2", new Color(0xFF4545));
    public static FloatValue arrayListX = new FloatValue("HUD", "ArrayList X Position", 5.0f, 1.0f, 100.0f, 1.0f);
    public static FloatValue arrayListY = new FloatValue("HUD", "ArrayList Y Position", 1.0f, 1.0f, 100.0f, 1.0f);
    public static FloatValue arrayListOffset = new FloatValue("HUD", "ArrayList Offset", 33.0f, 1.0f, 100.0f, 1.0f);
    public static FloatValue arrayListSpeed = new FloatValue("HUD", "ArrayList Fade Speed", 30.0f, 1.0f, 100.0f, 1.0f);
    public static FloatValue watermarkIndexSpeed = new FloatValue("HUD", "Watermark Fade Speed", 30.0f, 1.0f, 100.0f, 1.0f);
    public static BooleanValue arraylist = new BooleanValue("HUD", "Arraylist", Boolean.valueOf(true));
    public static BooleanValue arraylistLowercase = new BooleanValue("HUD", "Arraylist Lowercase", Boolean.valueOf(true));
    public static BooleanValue background = new BooleanValue("HUD", "Background", Boolean.valueOf(true));
    public static BooleanValue colorbar = new BooleanValue("HUD", "Color Bar", Boolean.valueOf(true));
    public static BooleanValue armor = new BooleanValue("HUD", "Show Armor HUD", Boolean.valueOf(true));
    public static BooleanValue effect = new BooleanValue("HUD", "Show Potion Effects", Boolean.valueOf(true));
    public static BooleanValue NoShader = new BooleanValue("Hud", "Disable Blur", Boolean.valueOf(true));
    public static BooleanValue renderRenderCategory = new BooleanValue("HUD", "Only Important", Boolean.valueOf(false));
    public static BooleanValue isLightMode = new BooleanValue("HUD", "Light Mode", Boolean.valueOf(false));

    public Hud() {
        super("Hud", Category.Render, 0, true, false);
    }

    @EventTarget
    public void onShader(EventShader eventShader) {
        float scale = 2.0f;
        float curWidth = (float)Hud.mc.displayWidth / scale;
        float curHeight = (float)Hud.mc.displayHeight / scale;
        if (effect.getValue().booleanValue()) {
            int x = 4;
            int y = (int)((float)(WorldRenderUtils.getScaledHeight() / 2) - FontManager.wqy18.FONT_HEIGHT);
            if (!Hud.mc.thePlayer.getActivePotionEffects().isEmpty()) {
                for (PotionEffect o : Hud.mc.thePlayer.getActivePotionEffects()) {
                    Potion potion = Potion.potionTypes[o.getPotionID()];
                    String potionName = I18n.format((String)potion.getName(), (Object[])new Object[0]);
                    String amplifierName = o.getAmplifier() == 0 ? "" : (o.getAmplifier() == 1 ? " " + I18n.format((String)"enchantment.level.2", (Object[])new Object[0]) : (o.getAmplifier() == 2 ? " " + I18n.format((String)"enchantment.level.3", (Object[])new Object[0]) : (o.getAmplifier() == 3 ? " " + I18n.format((String)"enchantment.level.4", (Object[])new Object[0]) : " " + o.getAmplifier())));
                    String text = potionName + amplifierName;
                    RenderUtil.drawRoundedRect((float)((float)x + 0.5f), (float)((float)y - 4.5f), (float)((float)x + FontManager.wqy18.getStringWidth(text) + 39.0f), (float)((float)y + 21.5f), (float)0.3f, (int)new Color(0, 0, 0, 40).getRGB());
                    y = (int)((float)y - (FontManager.wqy18.FONT_HEIGHT + 28.0f));
                }
            }
        }
        String themeValue = theme.getValue();
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
