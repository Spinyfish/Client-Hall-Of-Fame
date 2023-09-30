package axolotl.cheats.settings;

public class BooleanSetting extends Setting {

	public int index, code = 3;
	public boolean value;

	@Override
	public Object getObjectValue() {
		return value;
	}

	public BooleanSetting(String name, boolean startingBoolean) {
		super("BooleanSetting");
		this.name = name;
		this.value = startingBoolean;
	}

	@Override
	public void setValue(Object value) {
		this.value = (boolean)value;
	}
	
	public boolean isEnabled() {
		return value;
	}

	@Override
	public String getValue() {
		return name + ": " + (value ? "Enabled" : "Disabled");
	}

	public String getSpecificValue() {
		return getValue();
	}
	public boolean getBValue() {
		return value;
	}

	public void setValue(boolean b) {
		this.value = b;
	}
}
