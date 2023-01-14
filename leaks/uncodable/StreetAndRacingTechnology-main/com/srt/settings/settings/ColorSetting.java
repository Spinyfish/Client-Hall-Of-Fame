package com.srt.settings.settings;

import java.awt.Color;

import com.srt.module.ModuleBase;
import com.srt.settings.Setting;

public class ColorSetting extends Setting {

	private float hue;
	private float saturation;
	private float brightness;
	
	public ColorSetting(String name, Color defaultColor) {
		super(name, defaultColor);
		this.setColor(defaultColor);
	}

	@Override
	public Color getValue() {
		return Color.getHSBColor(hue, saturation, brightness);
	}
	
	public int getColor() {
		return this.getValue().getRGB();
	}
	
	public float getSaturation() {
		return this.saturation;
	}
	
	public float getBrightness() {
		return this.brightness;
	}
	
	public void setHue(float hue) {
		this.hue = hue;
	}
	
	public float getHue() {
		return this.hue;
	}
	
	public void setColor(Color color) {
		float[] colors = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		this.hue = colors[0];
		this.saturation = colors[1];
		this.brightness = colors[2];
	}
	
	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}
	
	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

}
