package de.verschwiegener.atero.design.font;

import java.awt.Color;

public class Font {

    String name;
    Fontrenderer fontrenderer;

    public Font(String name, java.awt.Font font, float fontsize) {
	this.name = name;
	this.fontrenderer = new Fontrenderer(font, fontsize, false, false);
    }

    public Font(String name, java.awt.Font font, float fontsize, boolean bold, boolean italic) {
	this.name = name;
	this.fontrenderer = new Fontrenderer(font, fontsize, bold, italic);
    }

    public String getName() {
	return name;
    }

    public Fontrenderer getFontrenderer() {
	return fontrenderer;
    }

    public void drawString(String text, int x, int y, Color color) {
	fontrenderer.drawString(text, x * 2, y * 2, color.getRGB());
    }
    public void drawString(String text, float x, float y, Color color) {
	fontrenderer.drawString(text, x * 2, y * 2, color.getRGB());
    }
    public void drawString(String text, float x, float y, int color) {
	fontrenderer.drawString(text, x * 2, y * 2, color);
    }

    public void drawString(String text, int x, int y, int color) {
	fontrenderer.drawString(text, x * 2, y * 2, color);
    }

    public void drawStringWithShadow(String text, int x, int y, int depth, Color color) {
	fontrenderer.drawStringWithShadows(text, x, y, depth, color.getRGB());
    }
    public int getStringWidth(String text) {
	return fontrenderer.getStringWidth(text);
    }
    public int getStringWidth2(String text) {
	return fontrenderer.getStringWidth(text) / 4;
    }
    public int getBaseStringHeight() {
	return fontrenderer.getBaseStringHeight();
    }
    public int getSpaceWidth() {
	return fontrenderer.getSpaceWidth();
    }

}
