package axolotl.cheats.settings;

import java.util.ArrayList;
import java.util.List;

public class Settings {

	public List<Setting> settings = new ArrayList<Setting>();
	
	public void addSetting(Setting s) {
		settings.add(s);
	}
	public void removeSetting(Setting s) {
		settings.remove(s);
	}
	public Object[] getSettingsArray() {
		return (Object[])settings.toArray();
	}
	public List<Setting> getSettings() {
		return settings;
	}
	public void addSettings(Setting... settings) {
		for (Setting s : settings) {
			this.addSetting(s);
		}
	}
	public void addSettings(Settings settings) {
		for(Object s : settings.getSettings().toArray()) {
			this.addSetting((Setting)s);
		}
	}
	
}
