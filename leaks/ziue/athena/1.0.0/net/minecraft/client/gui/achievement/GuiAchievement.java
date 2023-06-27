package net.minecraft.client.gui.achievement;

import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.stats.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;

public class GuiAchievement extends Gui
{
    private static final ResourceLocation achievementBg;
    private Minecraft mc;
    private int width;
    private int height;
    private String achievementTitle;
    private String achievementDescription;
    private Achievement theAchievement;
    private long notificationTime;
    private RenderItem renderItem;
    private boolean permanentNotification;
    
    public GuiAchievement(final Minecraft mc) {
        this.mc = mc;
        this.renderItem = mc.getRenderItem();
    }
    
    public void displayAchievement(final Achievement ach) {
        this.achievementTitle = I18n.format("achievement.get", new Object[0]);
        this.achievementDescription = ach.getStatName().getUnformattedText();
        this.notificationTime = Minecraft.getSystemTime();
        this.theAchievement = ach;
        this.permanentNotification = false;
    }
    
    public void displayUnformattedAchievement(final Achievement achievementIn) {
        this.achievementTitle = achievementIn.getStatName().getUnformattedText();
        this.achievementDescription = achievementIn.getDescription();
        this.notificationTime = Minecraft.getSystemTime() + 2500L;
        this.theAchievement = achievementIn;
        this.permanentNotification = true;
    }
    
    private void updateAchievementWindowScale() {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
    }
    
    public void updateAchievementWindow() {
        if (this.theAchievement != null && this.notificationTime != 0L && Minecraft.getMinecraft().thePlayer != null) {
            double d0 = (Minecraft.getSystemTime() - this.notificationTime) / 3000.0;
            if (!this.permanentNotification) {
                if (d0 < 0.0 || d0 > 1.0) {
                    this.notificationTime = 0L;
                    return;
                }
            }
            else if (d0 > 0.5) {
                d0 = 0.5;
            }
            this.updateAchievementWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            double d2 = d0 * 2.0;
            if (d2 > 1.0) {
                d2 = 2.0 - d2;
            }
            d2 *= 4.0;
            d2 = 1.0 - d2;
            if (d2 < 0.0) {
                d2 = 0.0;
            }
            d2 *= d2;
            d2 *= d2;
            final int i = this.width - 160;
            final int j = 0 - (int)(d2 * 36.0);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            this.mc.getTextureManager().bindTexture(GuiAchievement.achievementBg);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(i, j, 96, 202, 160, 32);
            if (this.permanentNotification) {
                this.mc.fontRendererObj.drawSplitString(this.achievementDescription, i + 30, j + 7, 120, -1);
            }
            else {
                this.mc.fontRendererObj.drawString(this.achievementTitle, i + 30, j + 7, -256);
                this.mc.fontRendererObj.drawString(this.achievementDescription, i + 30, j + 18, -1);
            }
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.renderItem.renderItemAndEffectIntoGUI(this.theAchievement.theItemStack, i + 8, j + 8);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }
    
    public void clearAchievements() {
        this.theAchievement = null;
        this.notificationTime = 0L;
    }
    
    static {
        achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    }
}
