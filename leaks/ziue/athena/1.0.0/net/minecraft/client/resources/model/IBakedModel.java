package net.minecraft.client.resources.model;

import net.minecraft.util.*;
import java.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.block.model.*;

public interface IBakedModel
{
    List<BakedQuad> getFaceQuads(final EnumFacing p0);
    
    List<BakedQuad> getGeneralQuads();
    
    boolean isAmbientOcclusion();
    
    boolean isGui3d();
    
    boolean isBuiltInRenderer();
    
    TextureAtlasSprite getParticleTexture();
    
    ItemCameraTransforms getItemCameraTransforms();
}
