package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;
import java.util.Collection;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
	
public class PotionStatusMod extends HudMod {
	
	protected float zLevelFloat;
	protected float zLevel;

	public PotionStatusMod() {
		super("Potion Status", "Display ur potion status", 100, 100);
	}
	
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        worldrenderer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
	
	@Override
	public void onRender2D() {
		int offsetX = 21;
        int offsetY = 14;
        int i = 80;
        int i2 = 16;
        ScaledResolution sr = new ScaledResolution(mc);
    	//BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, getWidth() + 2, getHeight() + 1, 3);
        DrawUtil.drawRoundedOutline(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 3, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		//this.drawShadow(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 2, 4.0f);
		//DrawUtil.drawRoundedRectangle(getX() - 1, getY() - 1, getWidth() + 1, getHeight() + 1, 3, new Color(255, 255, 255, 50));

		setonRenderBackground(getX() - 2, getY() - 1, getWidth() + 2, getHeight() + 1, 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
        Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();

        if (!collection.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int l = 33;
            if (this.mc.thePlayer.getActivePotionEffects() != null) {
        		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, getWidth() + 2, getHeight() + 1, 3);
        	}

            if (collection.size() > 5)
            {
                l = 132 / (collection.size() - 1);
            }

            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if (potion.hasStatusIcon())
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                    int i1 = potion.getStatusIconIndex();
                    drawTexturedModalRect((this.getX() + offsetX) - 20, (this.getY() + i2) - offsetY, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                String s1 = I18n.format(potion.getName(), new Object[0]);
                if (potioneffect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
                }

                fr.drawString(s1, this.getX() + offsetX, (this.getY() + i2) - offsetY, 16777215, true);
                String s = Potion.getDurationString(potioneffect);
                fr.drawString(s, this.getX() + offsetX, (this.getY() + i2 + 10) - offsetY, 8355711, true);
                i2 += l;
            }
        }
		
		super.onRender2D();
	}
	
	@Override
	public void onRenderDummy() {
		fr.drawString("Potion Status", this.getX(), this.getY(), -1);
		super.onRenderDummy();
	}
	
	@Override
    public int getWidth() {
        return 101;
    }

    @Override
    public int getHeight() {
        return 154;
    }

}
