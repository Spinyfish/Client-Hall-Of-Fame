package markgg.settings;

import markgg.modules.Module;

public class KeybindSetting extends Setting{

	public int code;
	private Module parent;

	public KeybindSetting(Module parent, int code) {
		this.name = "Keybind";
		this.parent = parent;
		this.code = code;
	}

	public int getKeyCode() {
		return code;
	}

	public void setKeyCode(int code) {
		this.code = code;
	}
	
	public Module getParentMod(){
		return parent;
	}

}