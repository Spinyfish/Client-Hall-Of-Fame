package intentions.settings;

import intentions.settings.Setting;

public class BooleanSetting extends Setting {

	public boolean enabled;

	public BooleanSetting(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void toggle() {
		enabled = !enabled;
	}
	
}
