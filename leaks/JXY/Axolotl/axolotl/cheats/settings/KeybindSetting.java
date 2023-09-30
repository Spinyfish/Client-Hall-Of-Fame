package axolotl.cheats.settings;

import org.lwjgl.input.Keyboard;

public class KeybindSetting extends Setting{

	private int code;
	public long timeKeychanged;
	
	@Override
	public String getValue() {
		String key = Keyboard.getKeyName(getCode()).replace("SEMICOLON", ";");
		return name + key;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
		this.timeKeychanged = System.currentTimeMillis();
	}

	@Override
	public Object getObjectValue() {
		return getCode();
	}

	@Override
	public void setValue(Object value) {
		code = (int)(Math.floor((double)value));
	}
	
	public KeybindSetting(int code) {
		super("KeybindSetting");
		this.name = "Keybind: ";
		this.code = code;
	}

	public String getSpecificValue() {
		return getCode() + "";
	}
	
}