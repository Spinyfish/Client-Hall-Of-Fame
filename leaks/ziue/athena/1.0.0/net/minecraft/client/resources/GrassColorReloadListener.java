package net.minecraft.client.resources;

import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import java.io.*;

public class GrassColorReloadListener implements IResourceManagerReloadListener
{
    private static final ResourceLocation LOC_GRASS_PNG;
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        try {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.readImageData(resourceManager, GrassColorReloadListener.LOC_GRASS_PNG));
        }
        catch (IOException ex) {}
    }
    
    static {
        LOC_GRASS_PNG = new ResourceLocation("textures/colormap/grass.png");
    }
}
