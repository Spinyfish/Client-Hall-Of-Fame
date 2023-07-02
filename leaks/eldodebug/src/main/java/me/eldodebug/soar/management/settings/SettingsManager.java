package me.eldodebug.soar.management.settings;

import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.Mod;

public class SettingsManager {
	
	private ArrayList<Setting> settings;
	
	public SettingsManager(){
		this.settings = new ArrayList<>();
	}
	
	public void addSetting(Setting in){
		this.settings.add(in);
	}
	
	public ArrayList<Setting> getSettingsByMod(Mod mod){
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : getSettings()){
			if(s.getParentMod().equals(mod)){
				out.add(s);
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}
	
	public Setting getSettingByNameForCliclGUI(String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name)){
				return set;
			}
		}
		return null;
	}
	
	public Setting getSettingByName(Mod mod, String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod){
				return set;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Setting getSettingByClass(Class<?> modClass, String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod().equals(Soar.instance.modManager.getModByClass((Class<Mod>) modClass))){
				return set;
			}
		}
		return null;
	}

	public ArrayList<Setting> getSettings() {
		return settings;
	}
}
