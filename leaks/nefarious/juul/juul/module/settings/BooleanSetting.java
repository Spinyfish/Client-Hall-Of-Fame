package juul.module.settings;

import juul.util.image.Animation;

public class BooleanSetting extends Setting {

	public boolean enabled;
	public Animation animation = new Animation();
	
	public BooleanSetting(String name, String desc, boolean enabled) {
		this.name = name;
		this.description = desc;
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
