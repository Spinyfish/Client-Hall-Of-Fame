package de.verschwiegener.atero.settings;

import java.awt.*;
import java.util.ArrayList;

public class SettingsItem {
	
	String name, current, child, childselect;
	float minValue, maxValue, currentValue;
	ArrayList<String> modes;
	boolean state;
	Category category;
	Color color;
	
	public SettingsItem(String name, float minValue, float maxValue, float currentValue, String child) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.currentValue = currentValue;
		this.child = child;
		this.category = Category.SLIDER;
	}

	public SettingsItem(String name, ArrayList<String> modes, String current, String child, String childselect) {
		this.name = name;
		this.modes = modes;
		this.current = current;
		this.child = child;
		this.childselect = childselect;
		this.category = Category.COMBO_BOX;
	}
	public SettingsItem(String name, boolean state, String child) {
		this.name = name;
		this.state = state;
		this.child = child;
		this.category = Category.CHECKBOX;
	}
	public SettingsItem(String name, Color color, String child) {
		this.name = name;
		this.color = color;
		this.child = child;
		this.category = Category.COLOR_PICKER;
	}
	public SettingsItem(String name, String currentText) {
		this.name = name;
		this.current = currentText;
		this.category = Category.TEXT_FIELD;
	}
	
	public String getName() {
		return name;
	}
	public Category getCategory() {
		return category;
	}
	public boolean isState() {
		return state;
	}
	public void toggleState() {
		state = !state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public float getCurrentValue() {
		return currentValue;
	}
	public float getMaxValue() {
		return maxValue;
	}
	public float getMinValue() {
		return minValue;
	}
	public void setCurrentValue(float currentValue) {
	    this.currentValue = currentValue;
	}
	public String getChild() {
		return child;
	}
	public ArrayList<String> getModes() {
		return modes;
	}
	public String getCurrent() {
		return current;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setCurrent(String current) {
		this.current = current;
	}
	public String getChildselect() {
	    return childselect;
	}
	
	public enum Category{

		SLIDER, COMBO_BOX, CHECKBOX, TEXT_FIELD, COLOR_PICKER, COLOR_PICKER2;
		
	}

}
