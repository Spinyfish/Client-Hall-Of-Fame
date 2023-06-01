package frapppyz.cutefurry.pics.modules.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.ColorUtil;
import frapppyz.cutefurry.pics.util.DiscordUtil;
import frapppyz.cutefurry.pics.util.HWIDUtil;
import frapppyz.cutefurry.pics.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HUD extends Mod {
    public Mode mode = new Mode("Mode", "Astra", "Astra", "Astra2", "Astra3", "Exhibition");
    private Boolean showBackground = new Boolean("Show Background", true);
    private Boolean showRender = new Boolean("Show Render", false);
    private Boolean showNotifications = new Boolean("Show Notifications", false);
    public static Mode suffixType = new Mode("Suffix Type", "Bracket", "Bracket", "Space", "None");

    public HUD() {
        super("HUD", "Draws arraylist etc.", 0, Category.RENDER);
        addSettings(mode, showBackground, showRender, suffixType, showNotifications);
        this.setToggledSilently(true);
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            if(mode.is("Astra")){
                final FontRenderer fr = mc.fontRendererObj;
                fr.drawString("A" + ChatFormatting.WHITE + "stra " + Wrapper.getAstra().ver, 4, 4, ColorUtil.getAstolfoColors(-12));

                if(System.currentTimeMillis() <= Wrapper.getNotifications().finishTime && showNotifications.isToggled()){
                    Gui.drawRect(mc.displayWidth/2-145, mc.displayHeight/2 - 75, mc.displayWidth/2, mc.displayHeight/2 - 26,  1342177280);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1.25, 1.25, 1.25);
                    fr.drawString(Wrapper.getNotifications().title, (int) (mc.displayWidth/3+47), (int) (mc.displayHeight/3 + 15), -1);
                    GlStateManager.popMatrix();
                    fr.drawString(Wrapper.getNotifications().message, (int) (mc.displayWidth/2-99), (int) (mc.displayHeight/2 - 52), -1);
                    fr.drawString(Wrapper.getNotifications().message2, (int) (mc.displayWidth/2-99), (int) (mc.displayHeight/2 - 37), -1);
                    this.mc.getTextureManager().bindTexture(new ResourceLocation("astra/notis/infoicon.png"));
                    Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, mc.displayWidth/2, mc.displayHeight/2, mc.displayWidth/2, mc.displayHeight/2, mc.displayWidth/2, mc.displayHeight/2);
                }

                float bpt = (float) (MathUtil.square(mc.thePlayer.posX - mc.thePlayer.lastTickPosX) + MathUtil.square(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ));
                float bps = (float) MathUtil.round((Math.sqrt(bpt) * 20) * mc.timer.timerSpeed, 0.01);

                fr.drawStringWithShadow("BPS: " + bps, 4, mc.displayHeight/2 - 10, -1);

                ArrayList<Mod> modules = new ArrayList<>();
                for(Mod m : Wrapper.getModManager().mods){
                    if(m.isToggled()) modules.add(m);
                }
                modules.sort(Comparator.comparingDouble(gay -> fr.getStringWidth(gay.getFullname())));
                Collections.reverse(modules);
                int count = 0;
                for(Mod m : modules){
                    final ScaledResolution sr = new ScaledResolution(mc);

                    if(!showRender.isToggled()){
                        if(m.getCategory() != Category.RENDER){
                            if(showBackground.isToggled()) Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getFullname()) - 6, count + 12, sr.getScaledWidth(), count, 0x85000000);

                            fr.drawString(m.getFullname(), mc.displayWidth / 2 - fr.getStringWidth(m.getFullname()) - 3, 3 + count, ColorUtil.getAstolfoColors(count*15));
                            count += 12;
                        }
                    }else{

                        if(showBackground.isToggled()) Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getFullname()) - 6, count + 12, sr.getScaledWidth(), count, 0x85000000);
                        fr.drawString(m.getFullname(), mc.displayWidth / 2 - fr.getStringWidth(m.getFullname()) - 3, 3 + count, ColorUtil.getAstolfoColors(count*15));
                        count += 12;
                    }

                }
            }else if(mode.is("Astra2")){
                final FontRenderer fr = mc.fontRendererObj;
                fr.drawString("D" + ChatFormatting.WHITE + "D (Duckware Destroyer)", 4, 4, Color.magenta.getRGB());

                fr.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), 4, mc.displayHeight/2 - 10, -1);

                ArrayList<Mod> modules = new ArrayList<>();
                for(Mod m : Wrapper.getModManager().mods){
                    if(m.isToggled()) modules.add(m);
                }
                modules.sort(Comparator.comparingDouble(gay -> fr.getStringWidth(gay.getFullname())));
                Collections.reverse(modules);
                int count = 0;
                for(Mod m : modules){
                    final ScaledResolution sr = new ScaledResolution(mc);
                    if(!showRender.isToggled()){
                        if(m.getCategory() != Category.RENDER){
                            if(showBackground.isToggled()) Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getFullname()) - 6, count + 12, sr.getScaledWidth(), count, 0x85000000);

                            fr.drawString(m.getFullname(), mc.displayWidth / 2 - fr.getStringWidth(m.getFullname()) - 3, 3 + count, -1);
                            count += 12;
                        }
                    }else{

                        if(showBackground.isToggled()) Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getFullname()) - 6, count + 12, sr.getScaledWidth(), count, 0x85000000);
                        fr.drawString(m.getFullname(), mc.displayWidth / 2 - fr.getStringWidth(m.getFullname()) - 3, 3 + count, -1);
                        count += 12;
                    }

                }
            }else if(mode.is("Astra3")){
                final FontRenderer fr = mc.fontRendererObj;
                fr.drawString("A" + ChatFormatting.WHITE + "stra " + Wrapper.getAstra().ver, 4, 4, ColorUtil.getAstraColors(0));

                if(System.currentTimeMillis() <= Wrapper.getNotifications().finishTime && showNotifications.isToggled()){
                    Gui.drawRect(mc.displayWidth/2-145, mc.displayHeight/2 - 75, mc.displayWidth/2, mc.displayHeight/2 - 26,  1342177280);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1.25, 1.25, 1.25);
                    fr.drawString(Wrapper.getNotifications().title, (int) (mc.displayWidth/3+47), (int) (mc.displayHeight/3 + 15), -1);
                    GlStateManager.popMatrix();
                    fr.drawString(Wrapper.getNotifications().message, (int) (mc.displayWidth/2-99), (int) (mc.displayHeight/2 - 52), -1);
                    fr.drawString(Wrapper.getNotifications().message2, (int) (mc.displayWidth/2-99), (int) (mc.displayHeight/2 - 37), -1);
                    this.mc.getTextureManager().bindTexture(new ResourceLocation("astra/notis/infoicon.png"));
                    Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, mc.displayWidth/2, mc.displayHeight/2, mc.displayWidth/2, mc.displayHeight/2, mc.displayWidth/2, mc.displayHeight/2);
                }

                float bpt = (float) (MathUtil.square(mc.thePlayer.posX - mc.thePlayer.lastTickPosX) + MathUtil.square(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ));
                float bps = (float) MathUtil.round((Math.sqrt(bpt) * 20) * mc.timer.timerSpeed, 0.01);

                fr.drawStringWithShadow("B" + ChatFormatting.WHITE + "PS: " + bps, 4, mc.displayHeight/2 - 20, ColorUtil.getAstraColors(-24));
                fr.drawStringWithShadow("F" + ChatFormatting.WHITE + "PS: " + Minecraft.getDebugFPS(), 4, mc.displayHeight/2 - 10, ColorUtil.getAstraColors(-12));

                ArrayList<Mod> modules = new ArrayList<>();
                for(Mod m : Wrapper.getModManager().mods){
                    if(m.isToggled()) modules.add(m);
                }
                modules.sort(Comparator.comparingDouble(gay -> fr.getStringWidth(gay.getFullname())));
                Collections.reverse(modules);
                int count = 0;
                for(Mod m : modules){
                    final ScaledResolution sr = new ScaledResolution(mc);

                    if(!showRender.isToggled()){
                        if(m.getCategory() != Category.RENDER){
                            if(showBackground.isToggled()) Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getFullname()) - 6, count + 12, sr.getScaledWidth(), count, 0x85000000);

                            fr.drawString(m.getFullname(), mc.displayWidth / 2 - fr.getStringWidth(m.getFullname()) - 3, 3 + count, ColorUtil.getAstraColors(count*15));
                            count += 12;
                        }
                    }else{

                        if(showBackground.isToggled()) Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getFullname()) - 6, count + 12, sr.getScaledWidth(), count, 0x85000000);
                        fr.drawString(m.getFullname(), mc.displayWidth / 2 - fr.getStringWidth(m.getFullname()) - 3, 3 + count, ColorUtil.getAstraColors(count*15));
                        count += 12;
                    }

                }
            }else if(mode.is("Exhibition")){
                final FontRenderer fr = mc.fontRendererObj;
                fr.drawString("E" + ChatFormatting.WHITE + "xhibition", 4, 4, ColorUtil.getAstraColors(0));

                ArrayList<Mod> modules = new ArrayList<>();
                for(Mod m : Wrapper.getModManager().mods){
                    if(m.isToggled()) modules.add(m);
                }
                modules.sort(Comparator.comparingDouble(gay -> fr.getStringWidth((gay.getSuffix() == null ? gay.getName() : gay.getName() + ChatFormatting.GRAY + " - " + gay.getSuffix()))));
                Collections.reverse(modules);

                int count = 0;
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.85, 0.85, 0.85);
                for(Mod m : modules){

                    if(!showRender.isToggled()){
                        if(m.getCategory() != Category.RENDER){


                            fr.drawString((m.getSuffix() == null ? m.getName() : m.getName() + ChatFormatting.GRAY + " - " + m.getSuffix()), (int) (mc.displayWidth / 1.7 - fr.getStringWidth((m.getSuffix() == null ? m.getName() : m.getName() + ChatFormatting.GRAY + " - " + m.getSuffix())) - 3), 3 + count, ColorUtil.getAstraColors(count*15));
                            count += 12;
                        }
                    }else{
                        fr.drawString((m.getSuffix() == null ? m.getName() : m.getName() + ChatFormatting.GRAY + " - " + m.getSuffix()), (int) (mc.displayWidth / 1.7 - fr.getStringWidth((m.getSuffix() == null ? m.getName() : m.getName() + ChatFormatting.GRAY + " - " + m.getSuffix())) - 3), 3 + count, ColorUtil.getAstraColors(count*15));
                        count += 12;
                    }
                }
                GlStateManager.popMatrix();
                fr.drawString(ChatFormatting.GRAY + "Developer build - " + ChatFormatting.WHITE + ChatFormatting.BOLD + HWIDUtil.UID, mc.displayWidth/2 - fr.getStringWidth(ChatFormatting.GRAY + "Developer build - " + ChatFormatting.WHITE + ChatFormatting.BOLD + HWIDUtil.UID), mc.displayHeight/2-10, Color.WHITE.getRGB());
                float bpt = (float) (MathUtil.square(mc.thePlayer.posX - mc.thePlayer.lastTickPosX) + MathUtil.square(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ));
                float bps = (float) MathUtil.round((Math.sqrt(bpt) * 20) * mc.timer.timerSpeed, 0.01);

                fr.drawStringWithShadow("BPS: " + bps, 4, mc.displayHeight/2 - 10, -1);
            }

        }
    }
}
