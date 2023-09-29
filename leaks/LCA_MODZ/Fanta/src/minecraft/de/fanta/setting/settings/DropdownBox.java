package de.fanta.setting.settings;

public class DropdownBox implements BaseSetting{
    public String curOption;
    private String[] options;

    public DropdownBox(String curOption, String[] options) {
        this.curOption = curOption;
        this.options = options;
    }

    public String[] getOptions() {
        return options;
    }

	@Override
	public Object getValue() {
		return curOption;
	}

	@Override
	public Object getMinValue() {
		return "";
	}

	@Override
	public Object getMaxValue() {
		return options;
	}

	@Override
	public void setValue(Object value) {
		this.curOption = (String) value;
	}

	@Override
	public void setMinValue(Object value) {
	}

	@Override
	public void setMaxValue(Object value) {
	}
}
