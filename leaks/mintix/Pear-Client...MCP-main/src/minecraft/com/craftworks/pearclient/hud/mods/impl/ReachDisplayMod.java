package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;
import java.text.DecimalFormat;

import com.craftworks.pearclient.event.EventTarget;
import com.craftworks.pearclient.event.impl.EventDamageEntity;
import com.craftworks.pearclient.event.impl.EventRender2D;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MovingObjectPosition;

public class ReachDisplayMod extends HudMod {
	
private DecimalFormat format = new DecimalFormat("0.##");
	
	private double distance = 0;
	private long hitTime =  -1;
	
	public ReachDisplayMod() {
		super("Reach Display", "Display your reach", 80, 10);
	}
	
	@Override
	public void onRender2D() {
		ScaledResolution sr = new ScaledResolution(mc);
		DrawUtil.drawRoundedOutline(getX() - 3, getY() - 4, getWidth() + 5, getHeight() + 6, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 3, getWidth() + 3, getHeight() + 4, 3);
		setonRenderBackground(getX() - 2, getY() - 3, getWidth() + 2, getHeight() + 4, 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
		fr.drawString(getText(), getX(), getY(), -1);
		super.onRender2D();
	}
	
	@Override
	public void onRenderDummy() {
		fr.drawString(getText(), getX(), getY(), -1);
		super.onRender2D();
	}
	
	@Override
		public int getWidth() {
			return fr.getStringWidth(getText());
		}
	
	@Override
	public int getHeight() {
		return fr.FONT_HEIGHT;
	}
	
	@EventTarget
	public void onDamageEntity(EventDamageEntity event) {
		if(mc.objectMouseOver != null && mc.objectMouseOver.hitVec != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			distance = mc.objectMouseOver.hitVec.distanceTo(mc.thePlayer.getPositionEyes(1.0F));
			hitTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public String getText() {
		if((System.currentTimeMillis() - hitTime) > 5000) {
			distance = 0;
		}
		if(distance == 0) {
			return "0.00 blocks";
		}else {
			return format.format(distance) + " blocks";
		}
	}

}
