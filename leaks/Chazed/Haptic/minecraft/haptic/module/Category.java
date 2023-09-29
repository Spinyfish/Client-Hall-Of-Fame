package haptic.module;

public enum Category {
	
	COMBAT("Combat", 40, 50),
	MOVEMENT("Movement", 150, 50),
	PLAYER("Player", 260, 50),
	WORLD("World", 370, 50),
	RENDER("Render", 480, 50),
	EXPLOIT("Exploit", 590, 50),
	GHOST("Ghost", 700, 50),
	MISC("Misc", 810, 50);
	
	private String name;
	private int x, y;
	private boolean mouseFocused, open = true;
	
	Category(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isMouseFocused() {
		return mouseFocused;
	}

	public void setMouseFocused(boolean mouseFocused) {
		this.mouseFocused = mouseFocused;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
}