package byron.Mono.interfaces;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.Event2D;
import byron.Mono.font.FontUtil;
import byron.Mono.module.Module;
import byron.Mono.module.ModuleManager;
import byron.Mono.utils.ColorUtils;
import byron.Mono.utils.RenderUtil;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Session;

import java.awt.*;
import java.sql.Time;
import java.util.Calendar;

import javax.swing.DebugGraphics;

import org.lwjgl.opengl.GL11;

public class HUD {

    public Minecraft mc = Minecraft.getMinecraft();

   

    public static Color getColorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        long now = (long) (speed * System.currentTimeMillis() + -index * timePerIndex);

        float rd = (firstColor.getRed() - secondColor.getRed()) / time;
        float gd = (firstColor.getGreen() - secondColor.getGreen()) / time;
        float bd = (firstColor.getBlue() - secondColor.getBlue()) / time;

        float rd2 = (secondColor.getRed() - firstColor.getRed()) / time;
        float gd2 = (secondColor.getGreen() - firstColor.getGreen()) / time;
        float bd2 = (secondColor.getBlue() - firstColor.getBlue()) / time;

        int re1 = Math.round(secondColor.getRed() + rd * (now % (long) time));
        int ge1 = Math.round(secondColor.getGreen() + gd * (now % (long) time));
        int be1 = Math.round(secondColor.getBlue() + bd * (now % (long) time));
        int re2 = Math.round(firstColor.getRed() + rd2 * (now % (long) time));
        int ge2 = Math.round(firstColor.getGreen() + gd2 * (now % (long) time));
        int be2 = Math.round(firstColor.getBlue() + bd2 * (now % (long) time));

        if (now % ((long) time * 2L) < (long) time) {
            return new Color(re2, ge2, be2);
        } else {
            return new Color(re1, ge1, be1);
        }
    }
    
    double posX;
    double posY;
    double width;
    double height;
    private int blockCount;
    private int slot;
   
   
    public static final void drawSmoothRect(float left, float top, float right, float bottom, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        Gui.drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Gui.drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        Gui.drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        Gui.drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        Gui.drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }
    public static Boolean isMonov4 = false;
    public static Boolean isMono = false;
    public static Boolean isByronix = false;
    public static Boolean isWish = false;
    public static Color color2 = new Color(255,25,255);
    public static Color color3 = new Color(55,25,25);
    int color = ColorUtils.astolfoColors(10, 1000);
    
    public void draw() {
    	 final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);
    	slot = mc.thePlayer.inventory.currentItem;
    	  int blocks = 0;
    	  blockCount = blocks;
    	if(isMono == true)
    	{
    		ScaledResolution sr;
       	 posX = 0;
       	 int count = 0;
       	 float count2 = 0.0F;
       	 double offset = (double)count2 * ((double)FontUtil.normal.getHeight() + 1.4D);
            posY = 0;
            sr = new ScaledResolution(this.mc);
            width = posX + 275;
            height = posY + 250;
           float rat = Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed;
           float bps = (float)(Math.round(this.mc.thePlayer.getDistance(this.mc.thePlayer.lastTickPosX, this.mc.thePlayer.posY, this.mc.thePlayer.lastTickPosZ) * (double)rat * 100.0D) / 100L);
           drawSmoothRect(4.0F, 0.5F, 190.0F, 13.0F, new Color(12,12,12,150).getRGB());
           drawSmoothRect(4.0F, 0.5F, 190.0F, 1.0F, ColorUtils.astolfoColors(count * 10, 1000));
           
           FontUtil.normal2.drawString("Mono", 8.0F, 2.0F, ColorUtils.astolfoColors(count * 10, 1000));
           FontUtil.litenormal.drawStringWithShadow(" b4", 39.0F, 2.5F, ColorUtils.astolfoColors(count * 10, 1000));
           FontUtil.normal.drawStringWithShadow(" |", 55.0F, 4.0F, ColorUtils.astolfoColors(count * 10, 1000));
           ++count;
           if (!mc.isSingleplayer())
           {
            FontUtil.normal.drawStringWithShadow(ChatFormatting.WHITE + mc.thePlayer.getName() + ChatFormatting.RESET + " | " + ChatFormatting.WHITE + mc.getCurrentServerData().serverIP, 65.0F, 4.0F, ColorUtils.astolfoColors(count * 10, 1000));

           }else
           {
           	 FontUtil.normal.drawStringWithShadow(ChatFormatting.WHITE + mc.thePlayer.getName() + ChatFormatting.RESET + " | " + ChatFormatting.WHITE + " ", 65.0F, 4.0F, ColorUtils.astolfoColors(count * 10, 1000));
           }
           
           FontUtil.normal.drawStringWithShadow("XYZ - " + ChatFormatting.WHITE + Math.round(this.mc.thePlayer.posX) + " " + Math.round(this.mc.thePlayer.posY) + " " + Math.round(this.mc.thePlayer.posZ), 0.0F, 500.0F, ColorUtils.astolfoColors(count * 10, 1000));
           count++;
           FontUtil.normal.drawStringWithShadow("FPS - " + ChatFormatting.WHITE + Minecraft.debugFPS, 0.0F, 490.0F, ColorUtils.astolfoColors(count * 10, 1000));
           count++;
           FontUtil.normal.drawStringWithShadow("BPS - " + ChatFormatting.WHITE + bps + " b/s", 0.0F, 480.0F, ColorUtils.astolfoColors(count * 10, 1000));
           count++;
           FontUtil.normal.drawStringWithShadow("ID - " + ChatFormatting.GRAY + "0000", 0.0F, 470.0F, new Color(90, 90, 90).getRGB());
           

           
           
           
           
           
           
           
           
           
           if(Mono.INSTANCE.getModuleManager().getModule("Scaffold").isToggled())
           {
           	
           	drawSmoothRect(390F, 248F, 450F, 247F, ColorUtils.astolfoColors(count * 10, 1000));
           	drawSmoothRect(390F, 259F, 450F, 248F, new Color(1, 1, 1, 120).getRGB());
           	FontUtil.litenormal.drawCenteredString(ChatFormatting.GRAY + "Blocks: " + ChatFormatting.WHITE + getItemSlot(true), (float) 420, (float) 250, new Color(255, 255, 255).getRGB());
           	count++;
           	
            final int height = sr.getScaledHeight() - 90;
            
      //      drawSmoothRect((float) (sr.getScaledWidth() / 2F) - 15, (float) height, 30F, 30F, astolfoColors(1000,10));

            FontUtil.normal.drawCenteredString(" " + getItemSlot(true), sr.getScaledWidth() / 2F, height + 19, -1);

           
            blockCount = blocks;
            
            
            

            
            if (itemStack != null) {
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, sr.getScaledWidth() / 2 - 8, height + 2);
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            } else
                FontUtil.normal.drawCenteredString(" ", sr.getScaledWidth() / 2F + 0.5F, height + 6, -1);
        }
           	
           

           Mono.INSTANCE.getModuleManager().getModules();

    }
    	
    	if(isByronix == true)
    	{
    		  FontUtil.normal2.drawString("Byronix ", 4.0F, 0.0F, new Color(250,250,250).getRGB());
    	        FontUtil.normal.drawString("(OP Bypass Edition) ", 4.0F, 14.0F, ColorUtils.astolfoColors(10, 100));
    	        FontUtil.normal2.drawString("B", 4.0F, 0.0F, ColorUtils.astolfoColors(10, 1000));
    	        FontUtil.normal.drawString("b4", 50.0F, 1.0F, ColorUtils.astolfoColors(10, 1000));
    	        FontUtil.normal.drawString("XYZ " + ChatFormatting.WHITE + Math.round(this.mc.thePlayer.posX) + " " + Math.round(this.mc.thePlayer.posY) + " " + Math.round(this.mc.thePlayer.posZ), 0.0F, 475.0F, color);
    	        FontUtil.normal.drawString("FPS " + ChatFormatting.WHITE + Minecraft.debugFPS, 0.0F, 485.0F, color);
    	        float rat = Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed;
    	        float bps = (float)(Math.round(this.mc.thePlayer.getDistance(this.mc.thePlayer.lastTickPosX, this.mc.thePlayer.posY, this.mc.thePlayer.lastTickPosZ) * (double)rat * 100.0D) / 100L);
    	        FontUtil.normal.drawString("Speed " + ChatFormatting.WHITE + bps + "b/s", 0.0F, 495.0F, color);

    	}
    
    
      	if(isWish == true)
    	{
    		ScaledResolution sr;
       	 posX = 0;
       	 int count = 0;
       	 float count2 = 0.0F;
       	 double offset = (double)count2 * ((double)FontUtil.normal.getHeight() + 1.4D);
            posY = 0;
            sr = new ScaledResolution(this.mc);
            width = posX + 275;
            height = posY + 250;
           float rat = Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed;
           float bps = (float)(Math.round(this.mc.thePlayer.getDistance(this.mc.thePlayer.lastTickPosX, this.mc.thePlayer.posY, this.mc.thePlayer.lastTickPosZ) * (double)rat * 100.0D) / 100L);
           drawSmoothRect(2.0F, 0.5F, 190.0F, 13.0F, -1878061297);
           drawSmoothRect(2.0F, 0.5F, 190.0F, 1.0F, ColorUtils.astolfoColors(count * 10, 1000));
           
           FontUtil.normal2.drawStringGradientLarge("Wish", (int) 4.0, (int) 2.0, new Color(220,12,12), new Color(100, 0, 0));
           FontUtil.normal.drawStringWithShadow(" |", 50.0F, 4.0F, ColorUtils.astolfoColors(10, 1000));
           if (!mc.isSingleplayer())
           {
            FontUtil.normal.drawStringWithShadow(ChatFormatting.WHITE + mc.thePlayer.getName() + ChatFormatting.RESET + " | " + ChatFormatting.WHITE + mc.getCurrentServerData().serverIP, 55.0F, 4.0F, ColorUtils.astolfoColors(10, 1000));

           }else
           {
           	 FontUtil.normal.drawStringWithShadow(ChatFormatting.WHITE + mc.thePlayer.getName() + ChatFormatting.RESET + " | " + ChatFormatting.WHITE + " ", 55.0F, 4.0F, ColorUtils.astolfoColors(10, 1000));
           }
           FontUtil.normal.drawStringWithShadow("XYZ: " + ChatFormatting.WHITE + Math.round(this.mc.thePlayer.posX) + " " + Math.round(this.mc.thePlayer.posY) + " " + Math.round(this.mc.thePlayer.posZ), 0.0F, 500.0F, ColorUtils.astolfoColors(10, 1000));
           FontUtil.normal.drawStringWithShadow("FPS: " + ChatFormatting.WHITE + Minecraft.debugFPS, 0.0F, 490.0F, ColorUtils.astolfoColors(10, 1000));
           FontUtil.normal.drawStringWithShadow("Speed: " + ChatFormatting.WHITE + bps + "b/s", 0.0F, 480.0F, ColorUtils.astolfoColors(10, 1000));
           

           
           
           
           
           
           
           
           
           
           if(Mono.INSTANCE.getModuleManager().getModule("Scaffold").isToggled())
           {
           	
           	drawSmoothRect(390F, 259F, 450F, 260F, ColorUtils.astolfoColors(1000, 10 * count));
           	drawSmoothRect(390F, 259F, 450F, 248F, new Color(1, 1, 1, 100).getRGB());
           	FontUtil.normal.drawCenteredStringWithShadow("Blocks: " + getItemSlot(true), (float) 420, (float) 250, 0);
           	count++;
           	
            final int height = sr.getScaledHeight() - 90;
            
      //      drawSmoothRect((float) (sr.getScaledWidth() / 2F) - 15, (float) height, 30F, 30F, astolfoColors(1000,10));

            FontUtil.normal.drawCenteredString(" " + getItemSlot(true), sr.getScaledWidth() / 2F, height + 19, -1);

           
            blockCount = blocks;
            
            
            

            
            if (itemStack != null) {
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, sr.getScaledWidth() / 2 - 8, height + 2);
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            } else
                FontUtil.normal.drawCenteredString(" ", sr.getScaledWidth() / 2F + 0.5F, height + 6, -1);
        }

           	
           

           Mono.INSTANCE.getModuleManager().getModules();

    }
          if (isMonov4) {
              String message = "Mono FPS: " + Minecraft.getDebugFPS() + " UID: 0000 ";
              int textWidth = Minecraft.fontRendererObj.getStringWidth(message);
              int textHeight = Minecraft.fontRendererObj.FONT_HEIGHT;
              Gui.drawRect(7, 7, 7 + textWidth + 8, 7 + textHeight + 4, new Color(58, 58, 63, 255).getRGB());
              Gui.drawRect(7, 7, 7 + textWidth + 8, 8, ColorUtils.astolfoColors(10,1000));
              Minecraft.fontRendererObj.drawString(message, 9, 9, new Color(255,255,255, 255).getRGB());
          }
    }
    
    private int getItemSlot(boolean count) {
        int itemCount = (count ? 0 : -1);

        for (int i = 8; i >= 0; i--) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                if (count) {
                    itemCount += itemStack.stackSize;
                } else {
                    itemCount = i;
                }
            }
        }

        return itemCount;
    }
    	
    }

