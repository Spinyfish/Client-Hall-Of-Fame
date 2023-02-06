package dev.elysium.base.mods.settings;

import dev.elysium.base.mods.Mod;

public class NumberSetting extends Setting{
	public double min;
	public double max;
	private double value;
	private double change;
	private boolean dragging;
	public NumberSetting(String name, double min, double max, double value, double change, Mod parent) {
		super(name, parent);
		this.min = min;
		this.max = max;
		this.value = value;
		this.change = change;
		this.dragging = false;
	}
	
	public double getValue() {
		return value;
	}
	public double getMin() {
		return min;
	}
	public double getMax() {
		return max;
	}
	
	public double getChange() {
		return change;
	}
	
	public void setValue(float value) {
		this.value = value;
		double precision = 1 / change;
		this.value = Math.round(Math.max(min, Math.min(max, value)) * precision) / precision;
	}
	public boolean isDragging() {
		return dragging;
	}
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}
}
