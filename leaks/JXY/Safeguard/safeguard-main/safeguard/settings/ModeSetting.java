package intentions.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {

	public int index;
	public List<String> modes;
	
	public ModeSetting(String name, String defaultMode, String[] modes) {
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
	}
	
	public String getMode() {
		if(index+1 > modes.size()-1 && index >= 0) {
			return modes.get(index);
		}
		return modes.get(index+1);
	}
	
	public boolean is(String mode) {
		return index == modes.indexOf(mode);
	}
	
	public void cycle() {
		if(index < modes.size()-1)index++; else index = -1;
	}
	
}
