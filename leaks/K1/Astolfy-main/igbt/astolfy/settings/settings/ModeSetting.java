package igbt.astolfy.settings.settings;

import java.util.ArrayList;

import igbt.astolfy.settings.Setting;

public class ModeSetting extends Setting<String[]> {
	private String currentValue;

	public ModeSetting(String name, String...values) {
		super(name, values);
		currentValue = values[0];
	}
	
	public String getCurrentValue() {
		return currentValue;
	}
	
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
}
