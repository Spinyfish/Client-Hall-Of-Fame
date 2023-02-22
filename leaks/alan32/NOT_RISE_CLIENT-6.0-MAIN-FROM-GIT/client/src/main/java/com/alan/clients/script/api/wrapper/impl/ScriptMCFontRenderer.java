package com.alan.clients.script.api.wrapper.impl;

import com.alan.clients.script.api.RenderAPI;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;

public class ScriptMCFontRenderer extends ScriptFontRenderer<FontRenderer>
{
	public ScriptMCFontRenderer(FontRenderer wrapped)
	{
		super(wrapped);
	}

	@Override
	public double width(String string)
	{
		return this.wrapped.width(string);
	}

	@Override
	public double height()
	{
		return FontRenderer.FONT_HEIGHT;
	}

	@Override
	public void draw(String string, double x, double y, int[] color)
	{
		this.wrapped.drawString(string, x, y, RenderAPI.intArrayToColor(color).getRGB());
	}

	@Override
	public void drawCentered(String string, double x, double y, int[] color)
	{
		this.wrapped.drawString(string, (float) x - ((int) width(string) >> 1), (float) y, RenderAPI.intArrayToColor(color).getRGB(), false);
	}

	@Override
	public void drawWithShadow(String string, double x, double y, int[] color)
	{
		this.wrapped.drawString(string, x, y, RenderAPI.intArrayToColor(color).getRGB(), true);
	}

	@Override
	public void drawCenteredWithShadow(String string, float x, float y, int[] color)
	{
		this.wrapped.drawString(string, x - ((int) width(string) >> 1), y, RenderAPI.intArrayToColor(color).getRGB(), true);
	}
}
