package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.draw.GLDraw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class FPSDisplayMod extends HudMod {

	public FPSDisplayMod() {
		super("FPS Display", "Display your Minecraft FPS", 10, 10);
	}
	
	@Override
	public void onRender2D() {
		ScaledResolution sr = new ScaledResolution(mc);
		DrawUtil.drawRoundedOutline(getX() - 3, getY() - 4, getWidth() + 5, getHeight() + 6, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 3, getWidth() + 3, getHeight() + 4, 3);
		setonRenderBackground(getX() - 2, getY() - 3, getWidth() + 2, getHeight() + 4, 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
		fr.drawString(getText(), getX(), getY(), new Color(255, 255, 255).getRGB());
		super.onRender2D();
	}
	
	@Override
	public void onRenderShadow() {
		super.onRenderShadow();
	}
	
	@Override
	public void onRenderDummy() {
		fr.drawString(getText(), getX(), getY(), -1);
		super.onRenderDummy();
	}
	
	@Override
	public int getHeight() {
		return fr.FONT_HEIGHT;
	}
	
	@Override
	public int getWidth() {
		return fr.getStringWidth(getText());
	}
	
	@Override
	public String getText() {
		return Minecraft.getDebugFPS() + " FPS";
	}
}
