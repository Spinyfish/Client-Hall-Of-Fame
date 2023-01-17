package com.alan.clients.script.api.wrapper.impl;

import com.alan.clients.script.api.wrapper.ScriptWrapper;

public abstract class ScriptFontRenderer<T> extends ScriptWrapper<T>
{
	public ScriptFontRenderer(T wrapped)
	{
		super(wrapped);
	}

	public abstract double width(String string);
	public abstract double height();
	public abstract void draw(String string, double x, double y, int[] color);
	public abstract void drawCentered(String string, double x, double y, int[] color);
	public abstract void drawWithShadow(String string, double x, double y, int[] color);
	public abstract void drawCenteredWithShadow(String string, float x, float y, int[] color);
}