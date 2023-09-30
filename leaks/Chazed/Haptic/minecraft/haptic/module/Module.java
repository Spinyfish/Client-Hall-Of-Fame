package haptic.module;

import java.util.ArrayList;
import java.util.Arrays;

import haptic.event.Event;
import haptic.setting.Setting;
import haptic.setting.impl.KeybindSetting;
import haptic.util.base.ModuleBaseUtil;
import net.minecraft.client.Minecraft;

public class Module implements ModuleBaseUtil {
	
	private String name;
	private String displayName;
	private Category category;
	private KeybindSetting keybind = new KeybindSetting(0, this);
	
	private ArrayList<Setting> settings = new ArrayList<>();
	
	private boolean settingsShowed;
	
	private boolean enabled;
	private ListeningType listeningType = ListeningType.ENABLED;
	
	public Module(String name, Category category, ListeningType listeningType, int key) {
		this.name = name;
		this.displayName = name;
		this.category = category;
		this.listeningType = listeningType;
		this.keybind.setKey(key);
		this.addSettings(keybind);
	}
	
	public Module(String name, Category category, ListeningType listeningType) {
		this.name = name;
		this.displayName = name;
		this.category = category;
		this.listeningType = listeningType;
		this.addSettings(keybind);
	}
	
	public Module(String name, Category category, int key) {
		this.name = name;
		this.displayName = name;
		this.category = category;
		this.keybind.setKey(key);
		this.addSettings(keybind);
	}
	
	public Module(String name, Category category) {
		this.name = name;
		this.displayName = name;
		this.category = category;
		this.addSettings(keybind);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
	}
	
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String newName) {
		this.displayName = newName;
	}

	public Category getCategory() {
		return category;
	}

	public KeybindSetting getKeybind() {
		return keybind;
	}
	
	public ListeningType getListeningType() {
		return listeningType;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if(enabled) {
			onEnable();
		} else {
			onDisable();
		}
	}
	
	public void setEnabledSilently(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void toggle() {
		this.enabled = !this.enabled;
		
		if(this.enabled) {
			onEnable();
		} else {
			onDisable();
		}
	}
	
	public void toggleSilently() {
		this.enabled = !this.enabled;
	}
	
	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}
	
	public ArrayList<Setting> getSettings() {
		return settings;
	}
	
	public boolean isSettingsShowed() {
		return settingsShowed;
	}
	
	public void setSettingsShowed(boolean showed) {
		this.settingsShowed = showed;
	}
	
	public Setting getSettingByName(String name) {
		for(Setting s : settings) {
			if(s.getName().equals(name)) {
				return s;
			}
		}
		
		return null;
	}
	
}