package com.craftworks.pearclient.util.setting.impl;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.setting.Setting;

public class NumberSetting extends Setting {
	public double value, minimum, maximum, increment;
	public HudMod parent;
	
	public NumberSetting(String name, double value, double minimum, double maximum, double increment,  HudMod parent) {
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		this.parent = parent;

		shouldRender = true;
	}

	public NumberSetting(String name, double value, double minimum, double maximum, double increment, HudMod parent, boolean shouldRender) {
		super();
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		this.parent = parent;
		shouldRender = false;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		double precision = 1 / increment;
		this.value = Math.round(Math.max(minimum, Math.min(maximum, value)) * precision) / precision;
	}

	public void setValueFromConfig(double value) {
		this.value = value;
	}
	
	public void increment(boolean positive) {
		setValue(getValue() + (positive ? 1 : -1) * increment);
	}

	public double getMinimum() {
		return minimum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	public double getIncrement() {
		return increment;
	}

	public void setIncrement(double increment) {
		this.increment = increment;
	}


	public double getNumber() {
		return value;
	}

	public void setNumber(double value) {
		this.value = value;

	}
}