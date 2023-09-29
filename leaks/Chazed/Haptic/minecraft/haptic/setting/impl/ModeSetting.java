package haptic.setting.impl;

import java.util.Arrays;
import java.util.List;

import haptic.module.Module;
import haptic.setting.Setting;

public class ModeSetting extends Setting {

	public int index;
	public List<String> modes;
	public Module parent;
	
	public ModeSetting(String name, Module parent, String defaultMode, String... modes) {
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
		this.parent = parent;
	}
	
	public ModeSetting(String name, String defaultMode, String... modes) {
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
		//this.parent = parent;
	}
	
	public String getMode() {
		return modes.get(index);
	}
	
	public boolean is(String mode) {
		return index == modes.indexOf(mode);
	}
	
	public void cycle() {
		if(index < modes.size() - 1) {
			index++;
		} else {
			index = 0;
		}
	}

	public void increment() {
		if(index < modes.size() - 1) {
			index++;
		} else {
			index = 0;
		}
	}
	
	public void decrement() {
		if(index > 0) {
			index--;
		} else {
			index = modes.size() - 1;
		}
	}

	public String getValueName() {
		return modes.get(index);
	}
	
	public String getName() {
		return name;
	}
	
	public void setMode(String mode) {
		boolean found = false;
		for(String s : modes) {
			if(s.equals(mode)) {
				found = true;
			}
		}
		if(found) {
			this.index = this.modes.indexOf(mode);
		}
    }
}
