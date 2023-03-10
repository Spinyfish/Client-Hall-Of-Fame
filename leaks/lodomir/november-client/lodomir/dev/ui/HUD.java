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
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.ModuleManager;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.MovementUtils;
import lodomir.dev.utils.render.ColorUtils;
import lodomir.dev.utils.render.RenderUtils;
import lodomir.dev.utils.world.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class HUD {
    public static String clientName = November.INSTANCE.name;

    public void draw() {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color = new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).getRGB();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        TimeUtils timer = new TimeUtils();
        TTFFontRenderer cfr = Interface.mode.isMode("Plain") ? November.INSTANCE.fm.getFont("SFR 20") : (Interface.mode.isMode("Exhibition") ? November.INSTANCE.fm.getFont("TAHOMA 20") : (Interface.mode.isMode("November") ? November.INSTANCE.fm.getFont("SFR 20") : (Interface.mode.isMode("November Lite") ? November.INSTANCE.fm.getFont("PRODUCT SANS 18") : November.INSTANCE.fm.getFont("PRODUCT SANS 20"))));
        TTFFontRenderer font = November.INSTANCE.fm.getFont("SFR 20");
        int dr = (int)Math.round(Interface.red.getValue());
        int dg = (int)Math.round(Interface.green.getValue());
        int db = (int)Math.round(Interface.blue.getValue());
        TTFFontRenderer getSize = November.INSTANCE.fm.getFont("PRODUCT SANS " + Interface.size.getValueInt());
        switch (Interface.font.getMode()) {
            case "Minecraft": {
                Object potionText;
                Potion potion;
                if (mc.gameSettings.showDebugInfo) break;
                if (Interface.watermark.isEnabled()) {
                    switch (Interface.mode.getMode()) {
                        case "Plain": {
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(Interface.size.getValueInt() / 12, Interface.size.getValueInt() / 12, Interface.size.getValueInt() / 12);
                            fr.drawStringWithShadow(clientName + (Interface.clock.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + ")" : ""), 3.0f, 3.0f, Interface.getColorInt());
                            GlStateManager.scale(1.0f, 1.0f, 1.0f);
                            GlStateManager.popMatrix();
                            break;
                        }
                        case "Exhibition": {
                            fr.drawStringWithShadow(clientName.charAt(0) + "\u00a7f" + HUD.removeFirstChar(clientName) + (Interface.clock.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + ")" : "") + (Interface.fps.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + ")" : ""), 3.0f, 3.0f, Interface.getColorInt());
                            break;
                        }
                        case "November": {
                            String info = " | " + November.INSTANCE.version + (Interface.info.isEnabled() ? " | " + November.INSTANCE.user : "") + " | " + ServerUtils.getCurrentServerIP() + (Interface.clock.isEnabled() ? " | " + dtf.format(now) : "");
                            RenderUtils.drawRect(font.getStringWidth(clientName + info) + 5, 16.0, 3.0, 4.0, new Color(0, 0, 0, 40).getRGB());
                            RenderUtils.drawRect(font.getStringWidth(clientName + info) + 5, 2.9, 3.0, 4.0, Interface.getColorInt());
                            fr.drawStringWithShadow(clientName + info, 4.0f, 5.0f, -1);
                            break;
                        }
                        case "Rise": {
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(1.5f, 1.5f, 1.5f);
                            fr.drawStringWithShadow(clientName, 3.0f, 5.0f, Interface.getColorInt());
                            GlStateManager.scale(1.0f, 1.0f, 1.0f);
                            GlStateManager.popMatrix();
                            fr.drawStringWithShadow(November.INSTANCE.version, (float)fr.getStringWidth(clientName) * 1.5f + 5.5f, 4.5f, Interface.getColorInt());
                            break;
                        }
                        case "November Lite": {
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(1.5f, 1.5f, 1.5f);
                            fr.drawStringWithShadow(clientName, 3.0f, 3.0f, Interface.getColorInt());
                            GlStateManager.scale(1.0f, 1.0f, 1.0f);
                            GlStateManager.popMatrix();
                            GlStateManager.pushMatrix();
                            fr.drawStringWithShadow("Lite", (float)((double)fr.getStringWidth(clientName) * 1.5 + 5.0), 4.0f, Color.white.getRGB());
                            GlStateManager.scale(1.0f, 1.0f, 1.0f);
                            GlStateManager.popMatrix();
                        }
                    }
                }
                if (!Interface.mode.isMode("Rise")) {
                    if (Interface.bps.isEnabled()) {
                        fr.drawStringWithShadow("BPS: " + ChatFormatting.WHITE + decimalFormat.format(MovementUtils.getBlocksPerSecond()), 2.0f, sr.getScaledHeight() - 10, Interface.getColorInt());
                    }
                    if (Interface.fps.isEnabled() && !Interface.mode.isMode("Exhibition")) {
                        fr.drawStringWithShadow("FPS: " + ChatFormatting.WHITE + mc.getDebugFPS(), 2.0f, sr.getScaledHeight() - 20, Interface.getColorInt());
                    }
                    if (Interface.info.isEnabled()) {
                        fr.drawStringWithShadow(ChatFormatting.GRAY + "Build: " + ChatFormatting.WHITE + November.INSTANCE.release + ChatFormatting.GRAY + " | UID: " + ChatFormatting.WHITE + "0001", sr.getScaledWidth() - fr.getStringWidth(ChatFormatting.GRAY + "Build: " + ChatFormatting.WHITE + November.INSTANCE.release + ChatFormatting.GRAY + " | UID: " + ChatFormatting.WHITE + "0001") - 1, sr.getScaledHeight() - 10, -1);
                    }
                    if (Interface.effects.isEnabled()) {
                        float potCount = 17.0f;
                        for (PotionEffect potionEffect : mc.thePlayer.getActivePotionEffects()) {
                            potion = Potion.potionTypes[potionEffect.getPotionID()];
                            potionText = I18n.format(potion.getName(), new Object[0]) + ChatFormatting.GRAY + " " + (potionEffect.getAmplifier() + 1) + ": " + Potion.getDurationString(potionEffect);
                            potCount += 9.0f;
                            if (Interface.info.isEnabled()) {
                                fr.drawStringWithShadow((String)potionText, sr.getScaledWidth() - fr.getStringWidth((String)potionText) - 1, (float)sr.getScaledHeight() - potCount + 5.0f, Interface.getColorInt());
                                continue;
                            }
                            fr.drawStringWithShadow((String)potionText, sr.getScaledWidth() - fr.getStringWidth((String)potionText) - 1, (float)sr.getScaledHeight() - potCount + 15.0f, Interface.getColorInt());
                        }
                    }
                } else {
                    if (Interface.bps.isEnabled()) {
                        fr.drawStringWithShadow("BPS: " + decimalFormat.format(MovementUtils.getBlocksPerSecond()), 1.5f, sr.getScaledHeight() - 10, Interface.getColorInt());
                    }
                    if (Interface.fps.isEnabled()) {
                        fr.drawStringWithShadow("FPS: " + mc.getDebugFPS(), 1.5f, sr.getScaledHeight() - 19, Interface.getColorInt());
                    }
                    if (Interface.effects.isEnabled()) {
                        float potCount = 17.0f;
                        for (PotionEffect potionEffect : mc.thePlayer.getActivePotionEffects()) {
                            potion = Potion.potionTypes[potionEffect.getPotionID()];
                            potionText = I18n.format(potion.getName(), new Object[0]) + " " + (potionEffect.getAmplifier() + 1) + ": " + Potion.getDurationString(potionEffect);
                            fr.drawStringWithShadow((String)potionText, (float)((double)(sr.getScaledWidth() - fr.getStringWidth((String)potionText)) - 0.5), (float)sr.getScaledHeight() - (potCount += 9.0f) + 15.0f, Interface.getColorInt());
                        }
                    }
                }
                ModuleManager mods = November.INSTANCE.getModuleManager();
                if (!Interface.mode.isMode("Rise")) {
                    November.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(m -> fr.getStringWidth(((Module)m).getDisplayName())).reversed());
                } else {
                    November.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(m -> fr.getStringWidth(((Module)m).getName())).reversed());
                }
                float index = 0.0f;
                double offsety = 0.0;
                for (Module m2 : mods.getModules()) {
                    if (!m2.isEnabled() && !m2.isVisible() || m2.category == Category.RENDER && Interface.norender.isEnabled()) continue;
                    if (Interface.border.isEnabled()) {
                        Gui.drawRect((double)(sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName())) - 4.3, offsety, sr.getScaledWidth(), offsety + (double)fr.FONT_HEIGHT + 2.0, new Color(0, 0, 0, 40).getRGB());
                    }
                    if (Interface.mode.isMode("November Lite")) {
                        fr.drawStringWithShadow(m2.getDisplayName(), sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName()) - 2, 3.0f + (float)fr.FONT_HEIGHT * index, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                    } else if (Interface.mode.isMode("Rise")) {
                        fr.drawStringWithShadow(m2.getName(), sr.getScaledWidth() - fr.getStringWidth(m2.getName()) - 5, 5.0f + (float)fr.FONT_HEIGHT * index, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                    } else {
                        fr.drawStringWithShadow(m2.getDisplayName(), (float)((double)(sr.getScaledWidth() - fr.getStringWidth(m2.getDisplayName()) - 2) + (Interface.sidebar.isMode("Right") ? -1.5 : 0.0)), (float)fr.FONT_HEIGHT * index + 3.0f, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                    }
                    index = (float)((double)index + 1.2);
                    offsety += (double)fr.FONT_HEIGHT + 1.86;
                    if (Interface.sidebar.isMode("Right")) {
                        Gui.drawRect((float)sr.getScaledWidth() - 1.5f, 0.0, sr.getScaledWidth(), offsety, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                        continue;
                    }
                    if (!Interface.sidebar.isMode("None")) continue;
                }
                break;
            }
            case "Client": {
                if (mc.gameSettings.showDebugInfo) break;
                if (Interface.watermark.isEnabled()) {
                    switch (Interface.mode.getMode()) {
                        case "Plain": {
                            getSize.drawStringWithShadow(clientName + (Interface.clock.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + ")" : ""), 1.0f, 1.0f, Interface.getColorInt());
                            break;
                        }
                        case "Exhibition": {
                            getSize.drawStringWithShadow(clientName.charAt(0) + "\u00a7f" + HUD.removeFirstChar(clientName) + (Interface.clock.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + dtf.format(now) + ChatFormatting.GRAY + ")" : "") + (Interface.fps.isEnabled() ? ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + ")" : ""), 1.0f, 1.0f, Interface.getColorInt());
                            break;
                        }
                        case "November": {
                            String info = " | " + November.INSTANCE.version + (Interface.info.isEnabled() ? " | " + November.INSTANCE.user : "") + " | " + ServerUtils.getCurrentServerIP() + (Interface.clock.isEnabled() ? " | " + dtf.format(now) : "");
                            RenderUtils.drawRect(cfr.getStringWidth(clientName + info) + 5, 16.0, 3.0, 4.0, new Color(0, 0, 0, 40).getRGB());
                            RenderUtils.drawRect(cfr.getStringWidth(clientName + info) + 5, 2.9, 3.0, 4.0, Interface.getColorInt());
                            cfr.drawStringWithShadow(clientName + info, 4.0f, 5.0f, -1);
                            break;
                        }
                        case "Rise": {
                            November.INSTANCE.fm.getFont("PRODUCT SANS 28").drawStringWithShadow(clientName, 3.0f, 5.0f, Interface.getColorInt());
                            cfr.drawStringWithShadow(November.INSTANCE.version, (double)November.INSTANCE.fm.getFont("PRODUCT SANS 28").getStringWidth(clientName) + 1.5, 4.5, Interface.getColorInt());
                            break;
                        }
                        case "November Lite": {
                            November.INSTANCE.fm.getFont("PRODUCT SANS 24").drawStringWithShadow(clientName, 3.0f, 3.0f, Interface.getColorInt());
                            November.INSTANCE.fm.getFont("PRODUCT SANS 14").drawStringWithShadow("Lite", (double)November.INSTANCE.fm.getFont("PRODUCT SANS 24").getStringWidth(clientName) + 1.4, 7.0, Color.white.getRGB());
                        }
                    }
                }
                if (Interface.bps.isEnabled()) {
                    cfr.drawStringWithShadow("BPS: " + ChatFormatting.WHITE + decimalFormat.format(MovementUtils.getBlocksPerSecond()), 1.0f, sr.getScaledHeight() - 10, Interface.getColorInt());
                }
                if (Interface.fps.isEnabled() && !Interface.mode.isMode("Exhibition")) {
                    cfr.drawStringWithShadow("FPS: " + ChatFormatting.WHITE + mc.getDebugFPS(), 1.0f, sr.getScaledHeight() - 20, Interface.getColorInt());
                }
                if (Interface.info.isEnabled()) {
                    cfr.drawStringWithShadow(ChatFormatting.GRAY + "Build: " + ChatFormatting.WHITE + November.INSTANCE.release + ChatFormatting.GRAY + " | UID: " + ChatFormatting.WHITE + "0001", sr.getScaledWidth() - cfr.getStringWidth(ChatFormatting.GRAY + "Build: " + ChatFormatting.WHITE + November.INSTANCE.release + ChatFormatting.GRAY + " | UID: " + ChatFormatting.WHITE + "0001"), sr.getScaledHeight() - 10, -1);
                }
                if (Interface.effects.isEnabled()) {
                    float potCount = 17.0f;
                    for (PotionEffect potionEffect : mc.thePlayer.getActivePotionEffects()) {
                        Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                        String potionText = I18n.format(potion.getName(), new Object[0]) + ChatFormatting.GRAY + " " + (potionEffect.getAmplifier() + 1) + ": " + Potion.getDurationString(potionEffect);
                        potCount += 9.0f;
                        if (Interface.info.isEnabled() && !Interface.mode.isMode("Astolfo")) {
                            cfr.drawStringWithShadow(potionText, sr.getScaledWidth() - cfr.getStringWidth(potionText), (float)sr.getScaledHeight() - potCount + 5.0f, Interface.getColorInt());
                            continue;
                        }
                        cfr.drawStringWithShadow(potionText, sr.getScaledWidth() - cfr.getStringWidth(potionText), (float)sr.getScaledHeight() - potCount + 15.0f, Interface.getColorInt());
                    }
                }
                ModuleManager mods = November.INSTANCE.getModuleManager();
                November.INSTANCE.moduleManager.modules.sort(Comparator.comparingInt(m -> cfr.getStringWidth(((Module)m).getDisplayName())).reversed());
                float index = 0.0f;
                double offsety = 0.0;
                for (Module m3 : mods.getModules()) {
                    if (!m3.isEnabled() && !m3.isVisible() || m3.category == Category.RENDER && Interface.norender.isEnabled()) continue;
                    if (Interface.border.isEnabled()) {
                        RenderUtils.drawRect((float)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) - 1.0f, offsety, sr.getScaledWidth(), offsety + (double)cfr.getHeight(m3.getDisplayName()) + 3.6, new Color(0, 0, 0, 40).getRGB());
                    }
                    if (!Interface.mode.isMode("November Lite")) {
                        cfr.drawStringWithShadow(m3.getDisplayName(), (float)((double)(sr.getScaledWidth() - cfr.getStringWidth(m3.getDisplayName())) + (Interface.sidebar.isMode("Right") ? -1.5 : 0.0)), (float)fr.FONT_HEIGHT * index + 1.0f, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                    } else if (Interface.mode.isMode("Rise")) {
                        cfr.drawStringWithShadow(m3.getName(), sr.getScaledWidth() - cfr.getStringWidth(m3.getName()) - 5, 5.0f + cfr.getHeight(m3.getName()) * index, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                    } else {
                        cfr.drawStringWithShadow(m3.getDisplayName(), 3.0f, 15.0f + cfr.getHeight(m3.getDisplayName()) * index, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                    }
                    index += 1.1f;
                    offsety += (double)cfr.getHeight(m3.getDisplayName());
                    if (Interface.sidebar.isMode("Right")) {
                        if (!Interface.mode.isMode("Rise")) {
                            RenderUtils.drawRect((float)sr.getScaledWidth() - 1.5f, 0.0, sr.getScaledWidth(), offsety, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                            continue;
                        }
                        RenderUtils.drawRect((float)sr.getScaledWidth() - 1.5f, 0.0, sr.getScaledWidth(), offsety - 3.0, Interface.color.isMode("Static") ? color : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(dr, dg, db), (int)offsety, (int)(offsety + 17.0 + 10.0)).getRGB() : (Interface.color.isMode("Rainbow") ? ColorUtils.rainbow((int)offsety * 20, 0.7f, 1.0).getRGB() : ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()), 3000.0f, (int)offsety, 55L, 4.0).getRGB())));
                        continue;
                    }
                    if (!Interface.sidebar.isMode("None")) continue;
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

