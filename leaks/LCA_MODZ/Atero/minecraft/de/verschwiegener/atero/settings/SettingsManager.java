package de.verschwiegener.atero.settings;

import java.util.ArrayList;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.SettingsItem.Category;

public class SettingsManager {

    ArrayList<Setting> settings = new ArrayList<>();

    public void addSetting(final Setting setting) {
	settings.add(setting);
    }

    public ArrayList<Module> getSearchResult(final String search) {
	final ArrayList<Module> result = new ArrayList<>();
	for (final Setting s : settings) {
	    for (SettingsItem item : s.getItems()) {
		if (!result.contains(s.getModule())) {
		    if (item.getName().contains(search)) {
			result.add(s.getModule());
			break;
		    }
		    if (item.getName().startsWith(search)) {
			result.add(s.getModule());
			break;
		    }
		    if (item.getCategory() == Category.COMBO_BOX) {
			for (String mode : item.getModes()) {
			    if (mode.startsWith(search)) {
				result.add(s.getModule());
				break;
			    }
			    if (mode.contains(search)) {
				result.add(s.getModule());
				break;
			    }
			}
		    }
		}
	    }
	}
	for (Module m : Management.instance.modulemgr.modules) {
	    if (m.getName().equalsIgnoreCase(search)) {
		if (!result.contains(m)) {
		    result.add(m);
		}
	    }
	}
	return result;

    }

    public ArrayList<Setting> getSettings() {
	return settings;
    }

    public Setting getSettingByName(final String name) {
	return settings.stream().filter(module -> module.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
		.findFirst().orElse(null);
    }

}
