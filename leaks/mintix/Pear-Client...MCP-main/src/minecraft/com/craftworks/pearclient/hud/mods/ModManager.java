package com.craftworks.pearclient.hud.mods;

import java.util.ArrayList;

import com.craftworks.pearclient.hud.mods.impl.*;

public class ModManager {

	private ArrayList<HudMod> mods = new ArrayList<HudMod>();

	public PotionStatusMod potionStatus;
	public FPSDisplayMod fpsDisplay;
	public ArmorStatusMod armorStatus;
	public ComboCounterMod comboCounter;
	public ReachDisplayMod reachDisplay;
	public ChatMod chat;
	public ToggleSprintMod toggleSprint;
	public SpeedometerMod speeddometer;
	public ZoomMod zoom;
	public KeystrokesMod keystrokes;
	public AutoGG autoGG;
	public CPSMod cps;
	public CoordsMod coords;
	public TNTCountdownMod tntCountdown;
	public WeatherChanger weatherChanger;
	public BlockInfoMod blockInfo;
	public ItemPhysicsMod itemPhysics;
	public ItemHeldStatusMod itemHeldStatus;
	public OldAnimation oldAnimation;
	
	public ModManager() {
		mods.add(potionStatus = new PotionStatusMod());
		mods.add(itemHeldStatus = new ItemHeldStatusMod());
		mods.add(fpsDisplay = new FPSDisplayMod());
		mods.add(armorStatus = new ArmorStatusMod());
		mods.add(comboCounter = new ComboCounterMod());
		mods.add(reachDisplay = new ReachDisplayMod());
		mods.add(chat = new ChatMod());
		mods.add(toggleSprint = new ToggleSprintMod());
		mods.add(speeddometer = new SpeedometerMod());
		mods.add(zoom = new ZoomMod());
		mods.add(oldAnimation = new OldAnimation());
		mods.add(keystrokes = new KeystrokesMod());
		mods.add(autoGG = new AutoGG());
		mods.add(cps = new CPSMod());
		mods.add(coords = new CoordsMod());
		mods.add(tntCountdown = new TNTCountdownMod());
		mods.add(blockInfo = new BlockInfoMod());
		mods.add(itemPhysics = new ItemPhysicsMod());
		mods.add(weatherChanger = new WeatherChanger());
	}
	
	public HudMod getModByName(String name) {
		return mods.stream().filter(mod -> mod.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public HudMod getModByClass(Class<?> modClass) {
		return mods.stream().filter(mod -> mod.getClass().equals(modClass)).findFirst().orElse(null);
	}
	
	public HudMod getModule(String name) {
		for (HudMod m : mods) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}

	public ArrayList<HudMod> getMods() {
		return mods;
	}
	
	public ArrayList<HudMod> getRenderModuleList() {
    	ArrayList<HudMod> mod = new ArrayList<>();
		for(HudMod tocase : mods) {
    		if(tocase instanceof HudMod) {
    			HudMod m = (HudMod) tocase;
    			mod.add(m);
    		}
    	}
		return mod;
	}
	
	public void renderMods() {
		for(HudMod m : mods) {
			if(m.isToggled()) {
				m.onRender2D();
				m.onRenderShadow();
			}
		}
	}
}
