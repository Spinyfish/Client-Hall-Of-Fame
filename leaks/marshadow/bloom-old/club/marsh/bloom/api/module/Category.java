package club.marsh.bloom.api.module;

import java.awt.Color;

public enum Category {
	PLAYER("Player",Color.BLUE,220),
	COMBAT("Combat",Color.RED,110),
	MOVEMENT("Movement",Color.MAGENTA,0),
	VISUAL("Render",Color.ORANGE,440),
	EXPLOIT("Exploit",Color.PINK,330),
	SCRIPT("Scripts", Color.BLACK,550);
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	boolean expanded = false;
	public boolean holding = false;
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	String name;
	Color color;
	public int x, y;
	Category(String name, Color color, int x) {
		this.name = name;
		this.color = color;
		this.y = 25;
		this.x = 25 + x;
	}
}
