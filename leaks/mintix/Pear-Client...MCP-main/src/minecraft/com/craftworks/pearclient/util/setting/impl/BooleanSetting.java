package com.craftworks.pearclient.util.setting.impl;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.setting.Setting;

public class BooleanSetting extends Setting {

	public boolean enabled;
	public HudMod parent;

	public BooleanSetting(String name, boolean enabled, HudMod parent) {
		this.name = name;
		this.enabled = enabled;
		this.parent = parent;

		shouldRender = true;
	}

	public BooleanSetting(String name,boolean enabled,HudMod parent, boolean shouldRender) {
		this.name = name;
		this.enabled = enabled;
		this.parent = parent;
		shouldRender = false;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean getValue() {
		return enabled;
	}
	
	public void toggle() {
		enabled = !enabled;
	}

	public boolean isOn() {
		return enabled;
	}
	
}