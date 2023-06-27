package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class RenderBlaze extends RenderLiving<EntityBlaze>
{
    private static final ResourceLocation blazeTextures;
    
    public RenderBlaze(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBlaze(), 0.5f);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityBlaze entity) {
        return RenderBlaze.blazeTextures;
    }
    
    static {
        blazeTextures = new ResourceLocation("textures/entity/blaze.png");
    }
}
