package net.minecraft.client.renderer.texture;

import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DynamicTexture extends AbstractTexture {
    private final int[] dynamicTextureData;

    /**
     * width of this icon in pixels
     */
    private final int width;

    /**
     * height of this icon in pixels
     */
    private final int height;

    public DynamicTexture(final BufferedImage bufferedImage) {
        this(bufferedImage.getWidth(), bufferedImage.getHeight());
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.dynamicTextureData, 0, bufferedImage.getWidth());
        this.updateDynamicTexture();
    }

    public DynamicTexture(final int textureWidth, final int textureHeight) {
        this.width = textureWidth;
        this.height = textureHeight;
        this.dynamicTextureData = new int[textureWidth * textureHeight];
        TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
    }

    public void loadTexture(final IResourceManager resourceManager) throws IOException {
    }

    public void updateDynamicTexture() {
        TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
    }

    public int[] getTextureData() {
        return this.dynamicTextureData;
    }
}
