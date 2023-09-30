package dev.elysium.client.scripting.api.components;

import java.awt.Color;

import org.luaj.vm2.LuaValue;

import dev.elysium.client.scripting.Script;

public class ButtonGuiComponent extends APIComponent{

	private int color = 0xff181818;
	private int width;
	private int height;
	private int cornerRadius = 0;
	private int textColor = -1;
	private String text;
	private String fontName;
	private int fontSize;
	public LuaValue click;
	public LuaValue starthover;
	public LuaValue endhover;
	public LuaValue hovering;
	
	public int paddingTop = 0;
	public int paddingBottom = 0;
	public int paddingRight = 0;
	public int paddingLeft = 0;
	
	public void SetPadding(int left, int top, int right, int bottom) {
		this.paddingLeft = left;
		this.paddingTop = top;
		this.paddingRight = right;
		this.paddingBottom = bottom;
	}
	
	public boolean wasHovering = false;
	public void Connect(String s, LuaValue f) {
		if(!f.isfunction())
			return;
		if(s.equalsIgnoreCase("click"))
			click = f;
		if(s.equalsIgnoreCase("starthover"))
			starthover = f;
		if(s.equalsIgnoreCase("endhover"))
			endhover = f;
		if(s.equalsIgnoreCase("hovering"))
			hovering = f;
		
		
	}
	
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
	
	public int GetColor() {
		return color;
	}

	public void SetColor(int color) {
		this.color = color;
	}
	public void SetColor(int r, int g, int b) {
		this.color = new Color(r, g, b).getRGB();
	}
	
	public int GetTextColor() {
		return textColor;
	}

	public void SetTextColor(int color) {
		this.textColor = color;
	}
	public void SetTextColor(int r, int g, int b) {
		this.textColor = new Color(r, g, b).getRGB();
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

	public ButtonGuiComponent(Script script) {
		super(script);
	}

}
