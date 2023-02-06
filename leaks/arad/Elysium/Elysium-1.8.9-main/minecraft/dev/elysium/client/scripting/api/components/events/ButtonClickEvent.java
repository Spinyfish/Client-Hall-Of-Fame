package dev.elysium.client.scripting.api.components.events;

public class ButtonClickEvent {

	private int mouseX;
	private int mouseY;
	private int mouseButton;
	

	public int GetMouseX() {
		return mouseX;
	}
	public int GetMouseY() {
		return mouseY;
	}
	public int GetMouseButton() {
		return mouseButton;
	}
	public ButtonClickEvent(int mouseX, int mouseY, int mouseButton) {
		super();
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.mouseButton = mouseButton;
	}
	
	
}
