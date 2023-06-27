package net.minecraft.client.renderer.texture;

import java.awt.image.*;
import optifine.*;
import shadersmod.client.*;
import net.minecraft.client.resources.*;
import java.io.*;

public class DynamicTexture extends AbstractTexture
{
    private final int[] dynamicTextureData;
    private final int width;
    private final int height;
    private static final String __OBFID = "CL_00001048";
    private boolean shadersInitialized;
    
    public DynamicTexture(final BufferedImage bufferedImage) {
        this(bufferedImage.getWidth(), bufferedImage.getHeight());
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.dynamicTextureData, 0, bufferedImage.getWidth());
        this.updateDynamicTexture();
    }
    
    public DynamicTexture(final int textureWidth, final int textureHeight) {
        this.shadersInitialized = false;
        this.width = textureWidth;
        this.height = textureHeight;
        this.dynamicTextureData = new int[textureWidth * textureHeight * 3];
        if (Config.isShaders()) {
            ShadersTex.initDynamicTexture(this.getGlTextureId(), textureWidth, textureHeight, this);
            this.shadersInitialized = true;
        }
        else {
            TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
        }
    }
    
    @Override
    public void loadTexture(final IResourceManager resourceManager) throws IOException {
    }
    
    public void updateDynamicTexture() {
        if (Config.isShaders()) {
            if (!this.shadersInitialized) {
                ShadersTex.initDynamicTexture(this.getGlTextureId(), this.width, this.height, this);
                this.shadersInitialized = true;
            }
            ShadersTex.updateDynamicTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height, this);
        }
        else {
            TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
        }
    }
    
    public int[] getTextureData() {
        return this.dynamicTextureData;
    }
}
