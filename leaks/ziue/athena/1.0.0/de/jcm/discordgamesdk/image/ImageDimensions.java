package de.jcm.discordgamesdk.image;

public class ImageDimensions
{
    private final int width;
    private final int height;
    
    ImageDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public String toString() {
        return "ImageDimensions{width=" + this.width + ", height=" + this.height + '}';
    }
}
