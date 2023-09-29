package dev.elysium.base.mods.settings;

import java.util.Arrays;

import dev.elysium.base.mods.Mod;

public class ModeSetting extends Setting{

	private int index = 0;
	public String[] modes;
	
	public ModeSetting(String name,Mod parent, String... modes) {
		super(name, parent);
		this.name = name;
		this.modes = modes;
		if(modes.length > 0)
			index = 0;
	}
	
	public String getMode() {
		if(index < 0)
			index = 0;
		return modes[index];
	}
	
	public boolean is(String mode) {
		if(index >= modes.length || index < 0)
			index = 0;
		return modes[index].equalsIgnoreCase(mode);
	}
	public boolean is(int index) {
		return this.index == index;
	}
	public int getIndex() {
		return index;
	}
	
	public void cycle() {
		if(index < modes.length -1) {
			index++;
		}else {
			index = 0;
		}
	}
	public void previous() {
		if(index > 0) {
			index--;
		}else {
			index = modes.length - 1;
		}
	}
	public void setMode(String mode) {
		this.index = Arrays.asList(modes).indexOf(mode);
	}
	
}