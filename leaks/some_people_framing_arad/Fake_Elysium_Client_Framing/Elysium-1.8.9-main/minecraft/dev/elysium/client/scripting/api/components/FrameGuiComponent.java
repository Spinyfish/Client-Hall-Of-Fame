package dev.elysium.client.scripting.api.components;

import java.awt.Color;

import dev.elysium.client.scripting.Script;

public class FrameGuiComponent extends APIComponent{

	private int color = -1;
	private int width;
	private int height;
	private int cornerRadius = 0;
	public boolean draggable = false;
	
	
	
	public int GetColor() {
		return color;
	}

	public void SetColor(int color) {
		this.color = color;
	}
	public void SetColor(int r, int g, int b) {
		this.color = new Color(r, g, b).getRGB();
	}
	
	public int GetCornerRadius() {
		return cornerRadius;
	}

	public void SetCornerRadius(int rounding) {
		this.cornerRadius = rounding;
	}

	public void SetSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int GetWidth() {
		return width;
	}

	public void SetWidth(int width) {
		this.width = width;
	}

	public int GetHeight() {
		return height;
	}

	public void SetHeight(int height) {
		this.height = height;
	}

	public FrameGuiComponent(Script script) {
		super(script);
	}

	
	
	
}
