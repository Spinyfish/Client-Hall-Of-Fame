package haptic.setting.impl;

import org.lwjgl.input.Keyboard;

import haptic.module.Module;
import haptic.setting.Setting;


public class KeybindSetting extends Setting {
	
	public int code;
	public Module parent;
	
	public KeybindSetting(int code, Module parent) {
		this.name = "Keybind";
		this.code = code;
		this.parent = parent;
	}

	public int getKey() {
		return code;
	}

	public void setKey(int key) {
		this.code = key;
	}

	public String getKeyName() {
		return Keyboard.getKeyName(code);
	}
	
}
