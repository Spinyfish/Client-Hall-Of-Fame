package me.eldodebug.soar.management.colors;

import java.awt.Color;

import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;

public class AccentColor {

	private String name;
	
	private Color color1, color2;

	public SimpleAnimation zoomAnimation = new SimpleAnimation(0.0F);
	public SimpleAnimation opacityAnimation = new SimpleAnimation(0.0F);
	
	public AccentColor(String name, Color color1, Color color2) {
		this.name = name;
		this.color1 = color1;
		this.color2 = color2;
	}
	
	public String getName() {
		return name;
	}

	public Color getColor1() {
		return color1;
	}

	public Color getColor2() {
		return color2;
	}
}
