package dev.elysium.base.mods;

public enum Category {

	RENDER("Render"), MOVEMENT("Movement"), PLAYER("Player"), COMBAT("Combat"), SETTINGS("Settings");

	public String name;
	public int selectIndex = 0;

	Category(String name) {
		this.name = name;
	}
}
