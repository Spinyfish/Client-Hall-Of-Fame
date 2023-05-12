package markgg.settings;

import markgg.modules.Module;

public class BooleanSetting extends Setting{

	public boolean enabled;
	private Module parent;

	public BooleanSetting(String name, Module parent,boolean enabled) {
		this.name = name;
		this.parent = parent;
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
	
	public Module getParentMod(){
		return parent;
	}
}
