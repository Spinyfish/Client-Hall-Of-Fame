package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;

import java.io.IOException;

public class GrassColorReloadListener implements IResourceManagerReloadListener {
    private static final ResourceLocation LOC_GRASS_PNG = new ResourceLocation("textures/colormap/grass.png");

    public void onResourceManagerReload(final IResourceManager resourceManager) {
        try {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.readImageData(resourceManager, LOC_GRASS_PNG));
        } catch (final IOException var3) {
        }
    }
}
