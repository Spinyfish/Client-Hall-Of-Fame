package axolotl.cheats.settings;

import axolotl.Axolotl;

public abstract class Setting {

	public static int amount = 0;

	public String name, sname; // Name of the setting + setting name for hardcode purposes
	public int code; // Code for the setting
	public boolean focused = false; // If the setting is focused in TabGUI
	public long id;

	public abstract Object getObjectValue();
	public abstract String getValue();
	public abstract String getSpecificValue();
	public abstract void setValue(Object value);

	public Setting(String sname) {
		this.sname = sname;
		this.id = ++amount;
	}

}