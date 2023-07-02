package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventScrollMouse;
import me.eldodebug.soar.management.events.impl.EventTick;
import me.eldodebug.soar.management.events.impl.EventZoomFov;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;

public class ZoomMod extends Mod{

	private SimpleAnimation zoomAnimation = new SimpleAnimation(0.0F);
	
	private boolean active;
	private float lastSensitivity;
	private float currentFactor = 1;
	
	public boolean wasCinematic;
	
	public ZoomMod() {
		super("Zoom", "Zoom in on the player's point of view", ModCategory.PLAYER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Scroll", this, false);
		this.addBooleanSetting("Smooth Zoom", this, false);
		this.addSliderSetting("Zoom Speed", this, 14, 5, 20, true);
		this.addSliderSetting("Factor", this, 4, 2, 15, true);
		this.addBooleanSetting("Smooth Camera", this, true);
	}
	
	@EventTarget
	public void onTick(EventTick event) {
		if(Soar.instance.keyBindManager.ZOOM.isKeyDown()) {
			if(!active) {
				active = true;
				lastSensitivity = mc.gameSettings.mouseSensitivity;
				resetFactor();
				wasCinematic = this.mc.gameSettings.smoothCamera;
				mc.gameSettings.smoothCamera = Soar.instance.settingsManager.getSettingByName(this, "Smooth Camera").getValBoolean();
				mc.renderGlobal.setDisplayListEntitiesDirty();
			}
		}else if(active) {
			active = false;
			setFactor(1);
			mc.gameSettings.mouseSensitivity = lastSensitivity;
			mc.gameSettings.smoothCamera = wasCinematic;
		}
	}
	
	@EventTarget
	public void onFov(EventZoomFov event) {
		
		boolean smoothZoom = Soar.instance.settingsManager.getSettingByName(this, "Smooth Zoom").getValBoolean();
		float zoomSpeed = Soar.instance.settingsManager.getSettingByName(this, "Zoom Speed").getValFloat() * 1.5F;
		
		zoomAnimation.setAnimation(currentFactor, zoomSpeed);

		event.setFov(event.getFov() * (smoothZoom ? zoomAnimation.getValue() : currentFactor));
	}
	
	@EventTarget
	public void onScroll(EventScrollMouse event) {
		if(active && Soar.instance.settingsManager.getSettingByName(this, "Scroll").getValBoolean()) {
			event.setCancelled(true);
			if(event.getAmount() < 0) {
				if(currentFactor < 0.98) {
					currentFactor+=0.03;
				}
			}else if(event.getAmount() > 0) {
				if(currentFactor > 0.06) {
					currentFactor-=0.03;
				}
			}
		}
	}
	
	public void resetFactor() {
		setFactor(1 / Soar.instance.settingsManager.getSettingByName(this, "Factor").getValFloat());
	}

	public void setFactor(float factor) {
		if(factor != currentFactor) {
			mc.renderGlobal.setDisplayListEntitiesDirty();
		}
		currentFactor = factor;
	}
}
