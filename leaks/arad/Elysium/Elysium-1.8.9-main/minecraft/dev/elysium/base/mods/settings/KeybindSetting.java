package dev.elysium.base.mods.settings;

import dev.elysium.base.mods.Mod;

public class KeybindSetting extends Setting{

	public int code;
	public int combo = 0;

	public KeybindSetting(int code, Mod parent)
	{
		super("Keybind", parent);
		this.code = code;
	}

	public KeybindSetting(int combo, int code, Mod parent) {
		super("Keybind", parent);
		this.code = code;
		this.combo = combo;
	}

	public int getKeyCode() {
		return code;
	}

	public void setKeyCode(int code) {
		this.code = code;
	}

}
