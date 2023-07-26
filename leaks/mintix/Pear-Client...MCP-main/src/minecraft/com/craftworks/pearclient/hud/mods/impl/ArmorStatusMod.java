package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.setting.impl.BooleanSetting;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmorStatusMod extends HudMod {
	
	BooleanSetting hori = new BooleanSetting("Horizontal", false, this);
	BooleanSetting AD = new BooleanSetting("Armor Damaged(Not Work with Horizontal)", true, this);

	public ArmorStatusMod() {
		super("Armor Status", "Display ur armor status", 40, 40);
		super.addSettings(hori, AD);
	}
	
	@Override
	public void onRender2D() {
		ScaledResolution sr = new ScaledResolution(mc);
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3);
		if(hori.isEnabled()) {
		     mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(3), getX(), getY() + 2);
		     mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(2), getX() + 20, getY() + 2);
		     mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(1), getX() + 40, getY() + 2);
		     mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(0), getX() + 60, getY() + 2);
		} else {
			for(int i = 0; i < mc.thePlayer.inventory.armorInventory.length; i++) {
				ItemStack itemStack = mc.thePlayer.inventory.armorInventory[i];
				renderItemStack(getX(), getY(), i, itemStack);
			}
		}
		DrawUtil.drawRoundedOutline(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 2, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		setonRenderBackground(getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
		super.onRender2D();
	}
	
	@Override
	public void onRenderShadow() {
		super.onRenderShadow();
	}
	
	@Override
	public void onRenderDummy() {
		if(hori.isEnabled()) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.diamond_helmet), getX(), getY() + 2);
			mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.diamond_chestplate), getX() + 20, getY() + 2);
			mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.diamond_leggings), getX() + 40, getY() + 2);
			mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.diamond_boots), getX() + 60, getY() + 2);
		} else {
			renderItemStack(getX(), getY(), 3, new ItemStack(Items.diamond_helmet));
			renderItemStack(getX(), getY(), 2, new ItemStack(Items.diamond_chestplate));
			renderItemStack(getX(), getY(), 1, new ItemStack(Items.diamond_leggings));
			renderItemStack(getX(), getY(), 0, new ItemStack(Items.diamond_boots));
		}
		super.onRenderDummy();
	}
	
	@Override
	public int getWidth() {
		if(hori.isEnabled()) {
		    return 78;
		} else {
			if(AD.isEnabled()) {
				return 45;
			} else {
				return 17;
			}
		}
	}

	@Override
	public int getHeight() {
		if(hori.isEnabled()) {
			return 20;
		} else {
			return 63;
		}
	}
	
	private void renderItemStack(int x, int y, int i, ItemStack is) {
			
			if(is == null) {
				return;
			}
			
			GL11.glPushMatrix();
			int yAdd = (-16 * i) + 48;
			if(!hori.isEnabled()) {
				if(AD.isEnabled()) {
					if(is.getItem().isDamageable()) {
						double damage = ((is.getMaxDamage() - is.getItemDamage()) / (double) is.getMaxDamage() * 100);
						fr.drawString(String.format("%.0f", damage) + "%", getX() + 20, getY() + yAdd + 5, -1);
		    		}
				}
			}
			
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y + yAdd);
			GL11.glPopMatrix();
			
		}

}
