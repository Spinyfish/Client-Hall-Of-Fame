package axolotl.cheats.settings;

import axolotl.cheats.config.JSONSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {

	public int index, code = 1;
	public List<String> modes;
	public List<SettingCluster> settingsCluster = new ArrayList<SettingCluster>();
	
	public ModeSetting(String name, String defaultMode, String... modes) {
		super("ModeSetting");
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
	}

	@Override
	public Object getObjectValue() {
		return getMode();
	}

	@Override
	public void setValue(Object value) {
		index = this.modes.indexOf(value.toString());
	}
	
	public String getMode() {
		try {
			return modes.get(index);
		} catch (Exception e){
			return "missingno";
		}
	}

	public List<Setting> getModeSettings() {

		try {
			return settingsCluster.get(index).settings.getSettings();
		} catch(Exception e) {
			return null;
		}

	}
	
	public boolean is(String mode) {
		return index == modes.indexOf(mode);
	}
	
	public void cycle() {
		if(index < modes.size() - 1) {
			index++;
		} else index = 0;
	}

	public Settings getSettingCluster(String string) {
		for(SettingCluster s : settingsCluster) {
			if(s.name.equalsIgnoreCase(string)) {
				return s.settings;
			}
		}
		SettingCluster s = new SettingCluster(string, new Settings());
		settingsCluster.add(s);
		return s.settings;
	}
	
	@Override
	public String getValue() {
		return name + ": " + modes.get(index);
	}

	public Settings getSettings() {
		return this.getSettingCluster(this.getMode());
	}

	public String getSpecificValue() {
		return getMode() + "";
	}

	public List<String> getModes() {
		return modes;
	}

	public void setValue(int index2) {
		index = index2;
	}

}
