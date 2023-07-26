package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;

import com.craftworks.pearclient.event.EventTarget;
import com.craftworks.pearclient.event.impl.EventAttackEntity;
import com.craftworks.pearclient.event.impl.EventClientTick;
import com.craftworks.pearclient.event.impl.EventDamageEntity;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.gui.ScaledResolution;

public class ComboCounterMod extends HudMod {
	
	private long hitTime = -1;
	private int combo;
	private int possibleTarget;

	public ComboCounterMod() {
		super("Combo Counter", "How Many?", 70, 70);
	}
	
	@EventTarget
	public void onTick(EventClientTick event) {
		if((System.currentTimeMillis() - hitTime) > 2000) {
			combo = 0;
		}
	}
	
	@EventTarget
	public void onAttackEntity(EventAttackEntity event) {
		possibleTarget = event.getEntity().getEntityId();
	}
	
	@EventTarget
	public void onDamageEntity(EventDamageEntity event) {
		if(event.getEntity().getEntityId() == possibleTarget) {
			combo++;
			possibleTarget = -1;
			hitTime = System.currentTimeMillis();
		}
		else if(event.getEntity() == mc.thePlayer) {
			combo = 0;
		}
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
		super.onRenderDummy();
	}
	
	@Override
	public int getWidth() {
		return fr.getStringWidth(getText());
	}
	
	@Override
	public int getHeight() {
		return fr.FONT_HEIGHT;
	}
	
	public String getText() {
			return combo + " Combo";
	}
	
}
