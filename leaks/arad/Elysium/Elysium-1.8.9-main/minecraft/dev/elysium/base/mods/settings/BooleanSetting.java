package dev.elysium.base.mods.settings;

import dev.elysium.base.mods.Mod;

public class BooleanSetting extends Setting{

	public boolean wasToggledInSwap = false;
	private boolean enabled;
	
	public BooleanSetting(String name, boolean enabled, Mod parent) {
		super(name, parent);
		this.enabled = enabled;
	}

	public void toggle()
	{
		enabled =! enabled;
	}

	public boolean isEnabled() {
		if(!isVisible())
			return false;
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
