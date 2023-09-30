package de.verschwiegener.atero.util;

public class GlyphMetrics {

    private double width;
    private double height;
    private int GLTextureID;

    public GlyphMetrics(double width, double height, int GLTextureID) {
	this.width = width;
	this.height = height;
	this.GLTextureID = GLTextureID;
    }

    public double getWidth() {
	return width;
    }

    public void setWidth(double width) {
	this.width = width;
    }

    public double getHeight() {
	return height;
    }

    public void setHeight(double height) {
	this.height = height;
    }

    public int getGLTextureID() {
	return GLTextureID;
    }

    public void setGLTextureID(int gLTextureID) {
	GLTextureID = gLTextureID;
    }

}
