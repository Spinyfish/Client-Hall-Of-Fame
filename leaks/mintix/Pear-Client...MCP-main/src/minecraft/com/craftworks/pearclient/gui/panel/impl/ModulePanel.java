package com.craftworks.pearclient.gui.panel.impl;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.gui.panel.comp.ModButton;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.font.GlyphPageFontRenderer;
import com.craftworks.pearclient.util.scroll.ScrollHelper;
import com.craftworks.pearclient.util.setting.Setting;
import com.craftworks.pearclient.util.setting.impl.BooleanSetting;
import com.craftworks.pearclient.util.setting.impl.ModeSetting;
import com.craftworks.pearclient.util.setting.impl.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class ModulePanel {
	
	ScrollHelper scrollHelper = new ScrollHelper();
	
	ArrayList<ModButton> modButtons = new ArrayList<>();
    public int scrollAmount = 0;

    public ModulePanelType type = ModulePanelType.MODULE;
    public HudMod configuring;

    public int x, y, w, h;

    public ModulePanel(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void draw(int mouseX, int mouseY) {
    	Calendar calendar;
    	SimpleDateFormat SPF = new SimpleDateFormat("hh:mm a");
    	String time = SPF.format(Calendar.getInstance().getTime());
    	
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        scrollHelper.setStep(5); //how much to increment each scroll
        scrollHelper.setElementsHeight(600); //height of all scrollable elements combined
        scrollHelper.setMaxScroll(200); //setmax scroll, usualy height of your scrollable are
        scrollHelper.setSpeed(100); //speed of smoothing scrolling
        scrollHelper.setFlag(true);
        
        float sc = scrollHelper.getScroll();
        
        GlyphPageFontRenderer renderer = GlyphPageFontRenderer.create("Arial Rounded MT Bold", 24, false, false, false);
        GlyphPageFontRenderer rendererbtn = GlyphPageFontRenderer.create("Arial Rounded MT Bold", 14, false, false, false);
        GlyphPageFontRenderer rendererinfo = GlyphPageFontRenderer.create("Arial Rounded MT Bold", 12, false, false, false);
        
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 200, sr.getScaledHeight() / 2 - 115.3f, 400, 231, 6, new Color(55, 55, 55));
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 200, sr.getScaledHeight() / 2 - 115, 85, 230, 6, new Color(33, 33, 33));
        DrawUtil.drawRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 116, 81, 232, new Color(33, 33, 33).getRGB());
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 105, 75, 55, 6, new Color(55, 55, 55));
        //btn
        //if(mouseX >= x -  && mouseX <= x + w - 615 && mouseY >= h - 45 + 3 && mouseY <= y + h - 372) {
        if(mouseX >= sr.getScaledWidth() / 2 - 195 && mouseX <= sr.getScaledWidth() / 2 - 120 && mouseY >= sr.getScaledHeight() / 2 - 45 + 3 && mouseY <= sr.getScaledHeight() / 2 - 22) {
        	DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 45 + 3, 75, 19, 6, new Color(77, 77, 77));
        	rendererbtn.drawString("Mods", sr.getScaledWidth() / 2 - 174, sr.getScaledHeight() / 2 - 37, -1, false);
        } else {
        	DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 45 + 3, 75, 19, 6, new Color(55, 55, 55));
        	rendererbtn.drawString("Mods", sr.getScaledWidth() / 2 - 174, sr.getScaledHeight() / 2 - 37, -1, false);
        }
        if(mouseX >= sr.getScaledWidth() / 2 - 195 && mouseX <= sr.getScaledWidth() / 2 - 120 && mouseY >= sr.getScaledHeight() / 2 - 20 + 3 && mouseY <= sr.getScaledHeight() / 2 + 4) {
	        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 20 + 3, 75, 19, 6, new Color(77, 77, 77));
	        rendererbtn.drawString("Cometics", sr.getScaledWidth() / 2 - 175, sr.getScaledHeight() / 2 - 12, -1, false);
        } else {
        	DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 20 + 3, 75, 19, 6, new Color(55, 55, 55));
	        rendererbtn.drawString("Cometics", sr.getScaledWidth() / 2 - 175, sr.getScaledHeight() / 2 - 12, -1, false);
        }
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 + 5 + 3, 75, 19, 6, new Color(55, 55, 55));
        rendererbtn.drawString("Not Available", sr.getScaledWidth() / 2 - 182, sr.getScaledHeight() / 2 + 13, -1, false);
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 + 30 + 3, 75, 19, 6, new Color(55, 55, 55));
        rendererbtn.drawString("Not Available", sr.getScaledWidth() / 2 - 182, sr.getScaledHeight() / 2 + 38, -1, false);
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 + 55 + 3, 75, 19, 6, new Color(55, 55, 55));
        rendererbtn.drawString("Not Available", sr.getScaledWidth() / 2 - 182, sr.getScaledHeight() / 2 + 63, -1, false);
        DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 + 80 + 3, 75, 19, 6, new Color(55, 55, 55));
        rendererbtn.drawString("Not Available", sr.getScaledWidth() / 2 - 182, sr.getScaledHeight() / 2 + 88, -1, false);
        //smt
        rendererinfo.drawString("V1.0Beta", x / 2 + 45, y / 2 + 230, new Color(60, 60, 60).getRGB(), false);
        //welcome, thank you
        rendererinfo.drawString("§fWelcome §a" + Minecraft.getMinecraft().thePlayer.getName() + "§f!", x / 2 + 30, y / 2 + 33, -1, false);
        rendererinfo.drawString("Thank you for using", x / 2 + 30, y / 2 + 43, -1, false);
        rendererinfo.drawString("§aPear§fClient", x / 2 + 40, y / 2 + 53, -1, false);
        //icon
        

        if(this.type == ModulePanelType.MODULE) {
            this.modButtons.clear();
            int xAdd = 0;
            int xFactor = 100;
            int yAdd = 0;
            int spots = 0;
            while ((spots * xFactor) < (300)) {
                spots++;
            }
            for (HudMod m : PearClient.instance.modManager.getMods()) {
                if (xAdd == (spots * xFactor) && xAdd != 0) {
                    xAdd = 0;
                    yAdd += 70;
                }
                //if(Minecraft.getMinecraft().thePlayer.getName() != "urmom") {
                	this.modButtons.add(new ModButton(sr.getScaledWidth() / 2 - 100 + xAdd, sr.getScaledHeight() / 2 - 15 + yAdd + sc, 90, 30, m));
				//}

                xAdd += xFactor;

            }
            DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 45, 300, 150, 6, new Color(33, 33, 33));
            DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 105, 300, 55, 6, new Color(33, 33, 33));
            DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 + 87, sr.getScaledHeight() / 2 - 103, 100, 50, 6, new Color(66, 66, 66));
            renderer.drawString(time, x / 2 + 330, y / 2 + 38, -1, false);
            	DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 45 + 3, 75, 19, 6, new Color(60, 232, 118));
            	rendererbtn.drawString("Mods", sr.getScaledWidth() / 2 - 174, sr.getScaledHeight() / 2 - 37, -1, false);
            
            int x2 = sr.getScaledWidth() / 2 - 250;
            int y2 = sr.getScaledHeight() / 2 - 65;
            startScissor(x2, y2 + 20 , 10000, 150);
            for (ModButton m : modButtons) {
                m.draw();
            }
            endScissor();
            } else if(this.type == ModulePanelType.CAPE) {
            	DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 + 100, sr.getScaledHeight() / 2 - 95, 90, 180, 6, new Color(33, 33, 33));
            	//btn
            	DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 105, sr.getScaledHeight() / 2 - 20 + 3, 75, 19, 6, new Color(33, 33, 33));
            	//smt
            	this.drawEntityOnScreen((x) / 2 + 365, (y) / 2 + 185, 70, 0, 0, Minecraft.getMinecraft().thePlayer);
            		DrawUtil.drawRoundedRectangle(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 20 + 3, 75, 19, 6, new Color(60, 232, 118));
            		rendererbtn.drawString("Cometics", sr.getScaledWidth() / 2 - 175, sr.getScaledHeight() / 2 - 12, -1, false);
            }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        DrawUtil.drawPicture(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 45 + 4, 18, 16, -1, "pearclient/icon/mods.png");
        DrawUtil.drawPicture(sr.getScaledWidth() / 2 - 195, sr.getScaledHeight() / 2 - 20 + 2, 18, 18, -1, "pearclient/icon/cosmetics.png");
        GlStateManager.disableBlend();
    }
    
    public static void startScissor(int x, int y, int width, int height) {
    	ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                x * sr.getScaleFactor(),
                (sr.getScaledHeight() - y) * sr.getScaleFactor() - height * sr.getScaleFactor(),
                width * sr.getScaleFactor(),
                height * sr.getScaleFactor()
        );
    }

    public static void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void setType(ModulePanelType lol, HudMod m) {
        if(lol == ModulePanelType.MODULE) {
            this.type = ModulePanelType.MODULE;
            this.configuring = null;
        } else if(lol == ModulePanelType.SETTING) {
            this.type = ModulePanelType.SETTING;
            this.configuring = m;
        } else if(lol == ModulePanelType.CAPE) {
            this.type = ModulePanelType.CAPE;
            this.configuring = null;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    	for(ModButton m : modButtons) {
            m.onClick(mouseX, mouseY, mouseButton);
        }
    		if(mouseX >= sr.getScaledWidth() / 2 - 195 && mouseX <= sr.getScaledWidth() / 2 - 120 && mouseY >= sr.getScaledHeight() / 2 - 20 + 3 && mouseY <= sr.getScaledHeight() / 2 + 4) {
    			this.setType(ModulePanelType.CAPE, null);
            } else if(mouseX >= sr.getScaledWidth() / 2 - 195 && mouseX <= sr.getScaledWidth() / 2 - 120 && mouseY >= sr.getScaledHeight() / 2 - 45 + 3 && mouseY <= sr.getScaledHeight() / 2 - 22) {
            	this.setType(ModulePanelType.MODULE, null);
            }
    	}
    
    public void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = 180;
        ent.rotationYaw = 180;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

}


