package com.srt.settings.settings;

import com.srt.settings.Setting;

public class NumberSetting extends Setting<Double> {
	
	private double rounding, min, max;

	public NumberSetting(String name, double value, double rounding, double min, double max) {
		super(name, value);
		this.rounding = rounding;
		this.min = min;
		this.max = max;
	}
	
	public double getRounding() {
		return rounding;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public void setMin(double min) {
		this.min = min;
	}

}
