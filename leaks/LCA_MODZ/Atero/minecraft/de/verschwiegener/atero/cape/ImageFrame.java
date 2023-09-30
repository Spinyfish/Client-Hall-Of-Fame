package de.verschwiegener.atero.cape;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class ImageFrame {

    private final int delay;
    private final BufferedImage image;
    private final String disposal;
    private DynamicTexture texture;
    private ResourceLocation location;

    public ImageFrame(BufferedImage image, int delay, String disposal, String name) {
	this.image = image;
	this.delay = delay;
	this.disposal = disposal;
	texture = new DynamicTexture(image);
	location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("GIF", texture);
    }

    public BufferedImage getImage() {
	return image;
    }

    public int getDelay() {
	return delay;
    }

    public String getDisposal() {
	return disposal;
    }
    public ResourceLocation getLocation() {
	return location;
    }

}
