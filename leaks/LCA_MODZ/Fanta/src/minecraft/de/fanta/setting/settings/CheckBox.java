package de.fanta.setting.settings;

public class CheckBox implements BaseSetting{
    public boolean state;

    public CheckBox(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

	@Override
	public Object getValue() {
		return state;
	}

	@Override
	public Object getMinValue() {
		return false;
	}

	@Override
	public Object getMaxValue() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		this.state = (boolean)value;
	}

	@Override
	public void setMinValue(Object value) {
	}

	@Override
	public void setMaxValue(Object value) {
	}
}
