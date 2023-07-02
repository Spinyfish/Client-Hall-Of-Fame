package me.eldodebug.soar.utils.color.impl;

import java.awt.Color;

public class HalloweenColorUtils {

	public Color getOrange(int opacity) {
		return new Color(255, 113, 0, opacity);
	}
	
	public Color getOrange() {
		return getOrange(255);
	}
	
	public Color getLightOrange(int opacity) {
		return new Color(253, 151, 2, opacity);
	}
	
	public Color getLightOrange() {
		return getLightOrange(255);
	}
	
	public Color getPurple(int opacity) {
		return new Color(174, 3, 255, opacity);
	}
	
	public Color getPurple() {
		return getPurple(255);
	}
	
	public Color getLightPurple(int opacity) {
		return new Color(225, 2, 255, opacity);
	}
	
	public Color getLightPurple() {
		return getLightPurple(255);
	}
	
	public Color getBlack(int opacity) {
		return new Color(0, 0, 0, opacity);
	}
	
	public Color getBlack() {
		return getBlack(255);
	}
}
