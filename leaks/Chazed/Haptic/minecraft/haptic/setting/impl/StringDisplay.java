package haptic.setting.impl;

import haptic.setting.Setting;

public class StringDisplay extends Setting {
	
	private String info;
	
	public StringDisplay(String info) {
		this.info = info;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
}