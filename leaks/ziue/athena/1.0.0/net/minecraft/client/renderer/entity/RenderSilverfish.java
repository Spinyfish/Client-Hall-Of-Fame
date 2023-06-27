package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class RenderSilverfish extends RenderLiving<EntitySilverfish>
{
    private static final ResourceLocation silverfishTextures;
    
    public RenderSilverfish(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSilverfish(), 0.3f);
    }
    
    @Override
    protected float getDeathMaxRotation(final EntitySilverfish entityLivingBaseIn) {
        return 180.0f;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntitySilverfish entity) {
        return RenderSilverfish.silverfishTextures;
    }
    
    static {
        silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");
    }
}
