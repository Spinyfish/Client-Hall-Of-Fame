package dev.elysium.client.scripting.api.components;

import java.awt.Color;

import dev.elysium.client.scripting.Script;

public class TextLabelComponent extends APIComponent{

	private int color = -1;
	private String text;
	private String fontName;
	private int fontSize;
	
	
	
	public String GetFontName() {
		return fontName;
	}

	public void SetFontName(String fontName) {
		this.fontName = fontName;
	}

	public void SetFont(String name, int size) {
		this.fontName = name;
		this.fontSize = size;
	}
	
	public int GetFontSize() {
		return fontSize;
	}

	public void SetFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String GetText() {
		return text;
	}

	public void SetText(String text) {
		this.text = text;
	}

	public TextLabelComponent(Script script) {
		super(script);
	}

	
	public int GetColor() {
		return color;
	}

	public void SetColor(int color) {
		this.color = color;
	}
	public void SetColor(int r, int g, int b) {
		this.color = new Color(r, g, b).getRGB();
	}

}
