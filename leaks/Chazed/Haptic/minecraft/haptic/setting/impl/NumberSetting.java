package haptic.setting.impl;

import haptic.module.Module;
import haptic.setting.Setting;

public class NumberSetting extends Setting {
	
	private double value;
	private final double minimum, maximum, increment;
	private Module parent;
	
	private boolean holdingMouseButton;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		double precision = 1 / increment;
		this.value = Math.round(Math.max(minimum, Math.min(maximum, value)) * precision) / precision;
	}
	
	public void increment(boolean positive) {
		setValue(getValue() + (positive ? 1 : -1) * increment);
	}

	public double getMinimum() {
		return minimum;
	}
	
	public double getMaximum() {
		return maximum;
	}

	public double getIncrement() {
		return increment;
	}

	public NumberSetting(String name, double value, double minimum, double maximum, double increment,  Module parent) {
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		this.parent = parent;
	}
	
	public NumberSetting(String name, double value, double minimum, double maximum, double increment) {
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
	}

	public String getName() {
		return name;
	}
	
}
