package com.srt.settings.settings;

import java.util.ArrayList;

import com.srt.settings.Setting;

public class ModeSetting extends Setting<ArrayList<String>> {
	private String currentValue;

	public ModeSetting(String name, ArrayList<String> values) {
		super(name, values);
		currentValue = values.get(0);
	}
	
	public String getCurrentValue() {
		return currentValue;
	}
	
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
}
