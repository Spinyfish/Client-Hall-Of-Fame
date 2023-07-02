package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventCameraRotation;
import me.eldodebug.soar.management.events.impl.EventKey;
import me.eldodebug.soar.management.events.impl.EventPlayerHeadRotation;
import me.eldodebug.soar.management.events.impl.EventTick;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.ClientUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class FreelookMod extends Mod{

	private boolean active;
	private float yaw;
	private float pitch;
	private int previousPerspective;
	private boolean toggled;
	
	public FreelookMod() {
		super("Freelook", "Move the viewpoint freely", ModCategory.PLAYER);
	}
	
	@Override
	public void setup() {
		
		ArrayList<String> options = new ArrayList<String>();
		
		options.add("Toggle");
		options.add("Keydown");
		
		this.addBooleanSetting("Invert", this, false);
		this.addModeSetting("Mode", this, "Keydown", options);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		ClientUtils.showNotification("Warning", "This mod is banned on some servers");
	}
	
	@EventTarget
	public void onTick(EventTick event) {
		
		String mode = Soar.instance.settingsManager.getSettingByName(this, "Mode").getValString();
		
		if(mode.equals("Keydown")) {
			if(Soar.instance.keyBindManager.FREELOOK.isKeyDown()) {
				start();
			}
			else {
				stop();
			}
		}
		
		if(mode.equals("Toggle")) {
			if(toggled) {
				start();
			}else {
				stop();
			}
		}
	}
	
	@EventTarget
	public void onKey(EventKey event) {
		
		String mode = Soar.instance.settingsManager.getSettingByName(this, "Mode").getValString();
		
		if(mode.equals("Toggle")) {
			if(Soar.instance.keyBindManager.FREELOOK.isPressed()) {
				toggled = !toggled;
			}
		}
		
		if(Keyboard.getEventKey() == mc.gameSettings.keyBindTogglePerspective.getKeyCode()) {
			toggled = false;
		}
	}
	
	@EventTarget
	public void onCameraRotation(EventCameraRotation event) {
		if(active) {
			event.setYaw(yaw);
			event.setPitch(pitch);
		}
	}
	
	@EventTarget
	public void onPlayerHeadRotation(EventPlayerHeadRotation event) {
		
		boolean invert = Soar.instance.settingsManager.getSettingByName(this, "Invert").getValBoolean();
		
		if(active) {
			float yaw = event.getYaw();
			float pitch = event.getPitch();
			event.setCancelled(true);
			pitch = -pitch;
			
			if(!invert) {
				pitch = -pitch;
			}
			
			if(invert) {
				 yaw = -yaw;
			}
			
			this.yaw += yaw * 0.15F;
			this.pitch = MathHelper.clamp_float(this.pitch + (pitch * 0.15F), -90, 90);
			mc.renderGlobal.setDisplayListEntitiesDirty();
		}
	}
	
	private void start() {
		if(!active) {
			active = true;
			previousPerspective = mc.gameSettings.thirdPersonView;
			mc.gameSettings.thirdPersonView = 3;
			Entity renderView = mc.getRenderViewEntity();
			yaw = renderView.rotationYaw;
			pitch = renderView.rotationPitch;
		}
	}
	
	private void stop() {
		if(active) {
			active = false;
			mc.gameSettings.thirdPersonView = previousPerspective;
			mc.renderGlobal.setDisplayListEntitiesDirty();
		}
	}
}
