package net.minecraft.client.resources;

import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import java.io.*;

public class FoliageColorReloadListener implements IResourceManagerReloadListener
{
    private static final ResourceLocation LOC_FOLIAGE_PNG;
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        try {
            ColorizerFoliage.setFoliageBiomeColorizer(TextureUtil.readImageData(resourceManager, FoliageColorReloadListener.LOC_FOLIAGE_PNG));
        }
        catch (IOException ex) {}
    }
    
    static {
        LOC_FOLIAGE_PNG = new ResourceLocation("textures/colormap/foliage.png");
    }
}
