/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package lodomir.dev.ui;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.ModuleManager;
import lodomir.dev.modules.impl.render.HUD;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.player.MovementUtils;
import lodomir.dev.utils.render.ColorUtils;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Overlay {
    public static String clientName = November.INSTANCE.name;
    public static float aheight;
    public static float fheight;
    long time = this.getTime();
    private long start;
    private long fadedIn;
    private long fadeOut;
    private long end;

    public int getPotionColor(Potion potion) {
        int potColor = new Color(-1).getRGB();
        if (potion == Potion.moveSpeed) {
            potColor = new Color(5874664).getRGB();
        } else if (potion == Potion.fireResistance) {
            potColor = new Color(14446422).getRGB();
        } else if (potion == Potion.damageBoost) {
            potColor = new Color(0xE14444).getRGB();
        } else if (potion == Potion.regeneration) {
            potColor = new Color(14566037).getRGB();
        } else if (potion == Potion.nightVision) {
            potColor = new Color(7297767).getRGB();
        } else if (potion == Potion.jump) {
            potColor = new Color(3268151).getRGB();
        } else if (potion == Potion.resistance) {
            potColor = new Color(3887072).getRGB();
        } else if (potion == Potion.absorption) {
            potColor = new Color(14461747).getRGB();
        } else if (potion == Potion.digSpeed) {
            potColor = new Color(14568945).getRGB();
        }
        return potColor;
    }

    private long getTime() {
        return System.currentTimeMillis() - this.start;
    }

    public void draw() {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        TTFFontRenderer cfr = November.INSTANCE.fm.getFont("SKID 10");
        ScaledResolution sr = new ScaledResolution(mc);
        int color = new Color(HUD.red.getValueInt(), HUD.green.getValueInt(), HUD.blue.getValueInt()).getRGB();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        LocalDateTime now = LocalDateTime.now();
        int dr = (int)Math.round(HUD.red.getValue());
        int dg = (int)Math.round(HUD.green.getValue());
        int db = (int)Math.round(HUD.blue.getValue());
        switch (HUD.font.getMode()) {
            case "Minecraft": {
                if (mc.gameSettings.showDebugInfo) break;
                switch (HUD.mode.getMode()) {
                    case "Astolfo": {
                        String name = clientName;
                        String info = " | " + November.INSTANCE.version + (HUD.info.isEnabled() ? " | " + November.INSTANCE.user : "") + (HUD.clock.isEnabled() ? " | " + dtf.format(now) : "") + (HUD.ping.isEnabled() ? " | ping: 1ms" : "");
                        RenderUtils.drawRect((double)fr.getStringWidth(name + info) + 6.5, 16.0, 3.0, 4.0, -1879048192);
                        RenderUtils.drawRect((double)fr.getStringWidth(name + info) + 6.5, 2.9, 3.0, 4.0, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)4, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : color)));
                        fr.drawStringWithShadow(name + info, 5.0f, 6.0f, -1);
                        break;
                    }
                    case "Plain": {
                        fr.drawStringWithShadow(clientName, 3.0f, 3.0f, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)4, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : color)));
                        break;
                    }
                    case "Exhibition": {
                        fr.drawStringWithShadow(clientName.charAt(0) + "\u00a7f" + Overlay.removeFirstChar(clientName) + (HUD.clock.isEnabled() ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + "]" : "") + (HUD.fps.isEnabled() ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "]" : "") + (HUD.ping.isEnabled() ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + "1 ms" + ChatFormatting.GRAY + "]" : ""), 1.0f, 3.0f, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)4, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : color)));
                    }
                }
                if (HUD.bps.isEnabled()) {
                    fr.drawStringWithShadow("BPS: " + ChatFormatting.WHITE + decimalFormat.format(MovementUtils.getBlocksPerSecond()), 2.0f, sr.getScaledHeight() - 10, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)1, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : new Color(HUD.red.getValueInt(), HUD.green.getValueInt(), HUD.blue.getValueInt()).getRGB())));
                }
                if (HUD.fps.isEnabled() && !HUD.mode.isMode("Exhibition")) {
                    fr.drawStringWithShadow("FPS: " + ChatFormatting.WHITE + mc.getDebugFPS(), 2.0f, sr.getScaledHeight() - 20, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)1, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : new Color(HUD.red.getValueInt(), HUD.green.getValueInt(), HUD.blue.getValueInt()).getRGB())));
                }
                if (HUD.info.isEnabled() && !HUD.mode.isMode("Astolfo")) {
                    fr.drawStringWithShadow("\u00a77" + November.INSTANCE.build + " - \u00a7l\u00a7f" + November.INSTANCE.release + "\u00a7r\u00a77 - " + November.INSTANCE.user, sr.getScaledWidth() - fr.getStringWidth("\u00a77" + November.INSTANCE.build + " - \u00a7f" + November.INSTANCE.release + "\u00a77 - " + November.INSTANCE.user) - 1, sr.getScaledHeight() - 10, -1);
                }
                if (HUD.effects.isEnabled()) {
                    float potCount = 17.0f;
                    for (PotionEffect potionEffect : mc.thePlayer.getActivePotionEffects()) {
                        Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                        String potionText = I18n.format(potion.getName(), new Object[0]) + ChatFormatting.GRAY + " " + (potionEffect.getAmplifier() + 1) + ": " + Potion.getDurationString(potionEffect);
                        int potColor = this.getPotionColor(potion);
                        potCount += 9.0f;
                        if (HUD.info.isEnabled()) {
                            fr.drawStringWithShadow(potionText, sr.getScaledWidth() - fr.getStringWidth(potionText) - 1, (float)sr.getScaledHeight() - potCount + 5.0f, potColor);
                            continue;
                        }
                        fr.drawStringWithShadow(potionText, sr.getScaledWidth() - fr.getStringWidth(potionText) - 1, (float)sr.getScaledHeight() - potCount + 15.0f, potColor);
                    }
                }
                ModuleManager mods = November.INSTANCE.getModuleManager();
                November.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(m -> fr.getStringWidth(((Module)m).getDisplayName())).reversed());
                float index = 0.0f;
                double offsety = 0.0;
                for (Module m2 : mods.getModules()) {
                    if (!m2.isEnabled() && !m2.isVisible()) continue;
                    fheight = fr.FONT_HEIGHT;
                    if (HUD.border.isEnabled()) {
                        Gui.drawRect((double)(sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName())) - 4.3, offsety, sr.getScaledWidth(), offsety + (double)fr.FONT_HEIGHT + 2.0, -1879048192);
                    }
                    fr.drawStringWithShadow(m2.getDisplayName(), sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName()) - 2, 3.0f + (float)fr.FONT_HEIGHT * index, HUD.color.isMode("Static") ? color : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)index, (int)(index + 17.0f + 10.0f)).getRGB() : (HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfoWave((int)4, (int)1, (long)((long)(index * 100.0f))) : ColorUtils.getRainbowWave((float)4.0f, (float)1.0f, (float)1.0f, (long)((long)index * 250L)))));
                    index = (float)((double)index + 1.2);
                    aheight = (float)(offsety += (double)fr.FONT_HEIGHT + 1.86);
                    if (HUD.sidebar.isEnabled()) {
                        Gui.drawRect((float)sr.getScaledWidth() - 1.5f, 0.0, sr.getScaledWidth(), offsety, HUD.color.isMode("Static") ? color : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)index, (int)(index + 17.0f + 10.0f)).getRGB() : (HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfoWave((int)4, (int)1, (long)((long)(index * 100.0f))) : ColorUtils.getRainbowWave((float)4.0f, (float)1.0f, (float)1.0f, (long)((long)index * 250L)))));
                        continue;
                    }
                    if (!HUD.outline.isEnabled()) continue;
                    Gui.drawRect((double)(sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName())) - 4.3, offsety, sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName()) - 6, (double)fr.FONT_HEIGHT + offsety + 2.0, HUD.color.isMode("Static") ? color : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)index, (int)(index + 17.0f + 10.0f)).getRGB() : (HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfoWave((int)4, (int)1, (long)((long)(index * 100.0f))) : ColorUtils.getRainbowWave((float)4.0f, (float)1.0f, (float)1.0f, (long)((long)index * 250L)))));
                }
                break;
            }
            case "Client": {
                if (mc.gameSettings.showDebugInfo) break;
                switch (HUD.mode.getMode()) {
                    case "Astolfo": {
                        String name = clientName;
                        String info = " | " + November.INSTANCE.version + (HUD.info.isEnabled() ? " | " + November.INSTANCE.user : "") + (HUD.clock.isEnabled() ? " | " + dtf.format(now) : "") + (HUD.ping.isEnabled() ? " | ping: 1ms" : "");
                        RenderUtils.drawRect(cfr.getStringWidth(name + info) + 5, 16.0, 3.0, 4.0, -1879048192);
                        RenderUtils.drawRect(cfr.getStringWidth(name + info) + 5, 2.9, 3.0, 4.0, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)4, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : color)));
                        cfr.drawStringWithShadow(name + info, 4.0f, 7.0f, -1);
                        break;
                    }
                    case "Plain": {
                        cfr.drawStringWithShadow(clientName + (HUD.clock.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + ")" : ""), 1.0f, 3.0f, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)4, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : color)));
                        break;
                    }
                    case "Exhibition": {
                        cfr.drawStringWithShadow(clientName.charAt(0) + "\u00a7f" + Overlay.removeFirstChar(clientName) + (HUD.clock.isEnabled() ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + "]" : "") + (HUD.fps.isEnabled() ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "]" : "") + (HUD.ping.isEnabled() ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + "1 ms" + ChatFormatting.GRAY + "]" : ""), 1.0f, 3.0f, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)4, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : color)));
                    }
                }
                if (HUD.bps.isEnabled()) {
                    cfr.drawStringWithShadow("BPS: " + ChatFormatting.WHITE + decimalFormat.format(MovementUtils.getBlocksPerSecond()), 2.0f, sr.getScaledHeight() - 10, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)1, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : new Color(HUD.red.getValueInt(), HUD.green.getValueInt(), HUD.blue.getValueInt()).getRGB())));
                }
                if (HUD.fps.isEnabled() && !HUD.mode.isMode("Exhibition")) {
                    cfr.drawStringWithShadow("FPS: " + ChatFormatting.WHITE + mc.getDebugFPS(), 2.0f, sr.getScaledHeight() - 20, HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfo((int)1, (int)1) : (HUD.color.isMode("Rainbow") ? ColorUtils.getRainbow((float)4.0f, (float)1.0f, (float)1.0f) : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), 100, 100).getRGB() : new Color(HUD.red.getValueInt(), HUD.green.getValueInt(), HUD.blue.getValueInt()).getRGB())));
                }
                if (HUD.info.isEnabled() && !HUD.mode.isMode("Astolfo")) {
                    cfr.drawStringWithShadow("\u00a77" + November.INSTANCE.build + " - \u00a7l\u00a7f" + November.INSTANCE.release + "\u00a7r\u00a77 - " + November.INSTANCE.user, sr.getScaledWidth() - cfr.getStringWidth("\u00a77" + November.INSTANCE.build + " - \u00a7f" + November.INSTANCE.release + "\u00a77 - " + November.INSTANCE.user) - 1, sr.getScaledHeight() - 10, -1);
                }
                if (HUD.effects.isEnabled()) {
                    float potCount = 17.0f;
                    for (PotionEffect potionEffect : mc.thePlayer.getActivePotionEffects()) {
                        Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                        String potionText = I18n.format(potion.getName(), new Object[0]) + ChatFormatting.GRAY + " " + (potionEffect.getAmplifier() + 1) + ": " + Potion.getDurationString(potionEffect);
                        int potColor = this.getPotionColor(potion);
                        potCount += 9.0f;
                        if (HUD.info.isEnabled() && !HUD.mode.isMode("Astolfo")) {
                            cfr.drawStringWithShadow(potionText, sr.getScaledWidth() - cfr.getStringWidth(potionText) - 1, (float)sr.getScaledHeight() - potCount + 5.0f, potColor);
                            continue;
                        }
                        cfr.drawStringWithShadow(potionText, sr.getScaledWidth() - cfr.getStringWidth(potionText) - 1, (float)sr.getScaledHeight() - potCount + 15.0f, potColor);
                    }
                }
                ModuleManager mods = November.INSTANCE.getModuleManager();
                November.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(m -> cfr.getStringWidth(((Module)m).getDisplayName())).reversed());
                float index = 0.0f;
                double offsety = 0.0;
                for (Module m3 : mods.getModules()) {
                    if (!m3.isEnabled() && !m3.isVisible()) continue;
                    fheight = cfr.getHeight(m3.getDisplayName());
                    if (HUD.border.isEnabled()) {
                        RenderUtils.drawRect((double)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) - 1.8, offsety, sr.getScaledWidth(), offsety + (double)cfr.getHeight(m3.getDisplayName()) + 3.6, new Color(100, 100, 100, 130).getRGB());
                    }
                    switch (HUD.color.getMode()) {
                        case "Astolfo": {
                            cfr.drawStringWithShadow(m3.getDisplayName(), (double)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) + (HUD.sidebar.isEnabled() ? -1.5 : 0.0), (double)(4.0f + (float)fr.FONT_HEIGHT * index), ColorUtils.getAstolfoWave((int)4, (int)1, (long)((long)(index * 100.0f))));
                            break;
                        }
                        case "Rainbow": {
                            cfr.drawStringWithShadow(m3.getDisplayName(), (double)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) + (HUD.sidebar.isEnabled() ? -1.5 : 0.0), (double)(4.0f + (float)fr.FONT_HEIGHT * index), ColorUtils.getRainbowWave((float)4.0f, (float)1.0f, (float)1.0f, (long)((long)(index * 100.0f))));
                            break;
                        }
                        case "Dynamic": {
                            cfr.drawStringWithShadow(m3.getDisplayName(), (double)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) + (HUD.sidebar.isEnabled() ? -1.5 : 0.0), (double)(4.0f + (float)fr.FONT_HEIGHT * index), ColorUtils.fade(new Color(dr, dg, db), 100, (int)(index + 17.0f + 10.0f)).getRGB());
                            break;
                        }
                        case "Static": {
                            cfr.drawStringWithShadow(m3.getDisplayName(), (double)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) + (HUD.sidebar.isEnabled() ? -1.5 : 0.0), (double)(4.0f + (float)fr.FONT_HEIGHT * index), color);
                        }
                    }
                    index += 1.2f;
                    aheight = (float)(offsety += (double)cfr.getHeight(m3.getDisplayName()) + 3.35);
                    if (!HUD.sidebar.isEnabled()) continue;
                    RenderUtils.drawRect((float)sr.getScaledWidth() - 1.5f, 0.0, sr.getScaledWidth(), offsety, HUD.color.isMode("Static") ? color : (HUD.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)index, (int)(index + 17.0f + 10.0f)).getRGB() : (HUD.color.isMode("Astolfo") ? ColorUtils.getAstolfoWave((int)4, (int)1, (long)((long)(index * 100.0f))) : ColorUtils.getRainbowWave((float)4.0f, (float)1.0f, (float)1.0f, (long)((long)index * 250L)))));
                }
                break;
            }
        }
    }

    public static String removeFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(1);
    }
}

