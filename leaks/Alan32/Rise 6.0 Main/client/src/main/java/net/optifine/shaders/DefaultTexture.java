package net.optifine.shaders;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

public class DefaultTexture extends AbstractTexture {
    public DefaultTexture() {
        this.loadTexture(null);
    }

    public void loadTexture(final IResourceManager resourcemanager) {
        final int[] aint = ShadersTex.createAIntImage(1, -1);
        ShadersTex.setupTexture(this.getMultiTexID(), aint, 1, 1, false, false);
    }
}
