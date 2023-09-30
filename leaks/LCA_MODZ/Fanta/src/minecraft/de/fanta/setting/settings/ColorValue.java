package de.fanta.setting.settings;

import java.awt.Color;


public class ColorValue implements BaseSetting{

	public int color = Color.white.getRGB();

	public ColorValue(int color) {
		this.color = color;
	}

	@Override
	public Object getValue() {
		return color;
	}

	@Override
	public Object getMinValue() {
		return Color.black.getRGB();
	}

	@Override
	public Object getMaxValue() {
		return Color.white.getRGB();
	}

	@Override
	public void setValue(Object value) {
		this.color = (Integer)value;
	}

	@Override
	public void setMinValue(Object value) {
	}

	@Override
	public void setMaxValue(Object value) {
	}
	
	
}
