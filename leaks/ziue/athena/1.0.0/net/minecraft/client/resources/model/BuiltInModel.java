package net.minecraft.client.resources.model;

import net.minecraft.util.*;
import java.util.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;

public class BuiltInModel implements IBakedModel
{
    private ItemCameraTransforms cameraTransforms;
    
    public BuiltInModel(final ItemCameraTransforms p_i46086_1_) {
        this.cameraTransforms = p_i46086_1_;
    }
    
    @Override
    public List<BakedQuad> getFaceQuads(final EnumFacing p_177551_1_) {
        return null;
    }
    
    @Override
    public List<BakedQuad> getGeneralQuads() {
        return null;
    }
    
    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }
    
    @Override
    public boolean isGui3d() {
        return true;
    }
    
    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }
    
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }
    
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.cameraTransforms;
    }
}
