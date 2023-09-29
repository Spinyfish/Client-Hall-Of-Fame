package de.fanta.setting.settings;

public interface BaseSetting {

	public Object getValue();
	public Object getMinValue();
	public Object getMaxValue();
	public void setValue(Object value);
	public void setMinValue(Object value);
	public void setMaxValue(Object value);
	
}
