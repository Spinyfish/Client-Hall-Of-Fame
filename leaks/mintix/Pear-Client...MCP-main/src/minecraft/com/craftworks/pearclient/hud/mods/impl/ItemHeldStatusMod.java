package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemHeldStatusMod extends HudMod {
	
	public ItemHeldStatusMod() {
		super("Item Held Status", "hmm", 30, 60);
	}

	@Override
	public int getWidth() {
		return 60;
	}

	@Override
	public int getHeight() {
		return 15;
	}

	@Override
	public void onRender2D() {
		ItemStack item = mc.thePlayer.getHeldItem();
		ScaledResolution sr = new ScaledResolution(mc);
		DrawUtil.drawRoundedOutline(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 6, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, getWidth() + 2, getHeight() + 4, 3);
		setonRenderBackground(getX() - 2, getY() - 1, getWidth() + 2, getHeight() + 4, 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
		renderItemStack(2, item);
		super.onRender2D();
		
	}
	@Override 
	public void onRenderDummy() {
		renderItemStack(2, new ItemStack(Items.diamond_sword));
		super.onRenderDummy();
	}

	private void renderItemStack(int i, ItemStack is) {
		
		if(is == null	) {
			return;
		}
		GL11.glPushMatrix();
		int yAdd = (-16 * 3) + 48;
		if(mc.thePlayer !=null && is !=null) {
		   if(is.getItem().isDamageable()) {
			  double damage = (is.getMaxDamage() - is.getItemDamage()) / (double) is.getMaxDamage() * 100;
			  fr.drawString(String.format("%.2f%%", damage), getX() + 20, getY() + yAdd + 5, -1);
		  }
		
		  if(is.isStackable() && mc.thePlayer.getHeldItem().stackSize !=1) {
			  fr.drawString(Integer.toString(mc.thePlayer.getHeldItem().stackSize), getX() + 20, getY() + yAdd + 5, -1);
			
		  }
		 
		  RenderHelper.enableGUIStandardItemLighting();
		  mc.getRenderItem().renderItemAndEffectIntoGUI(is, getX(), getY() + yAdd);
		  GL11.glPopMatrix();
		}
	}

}
