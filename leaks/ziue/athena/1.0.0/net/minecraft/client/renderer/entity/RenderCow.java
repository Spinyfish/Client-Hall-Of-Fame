package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class RenderCow extends RenderLiving<EntityCow>
{
    private static final ResourceLocation cowTextures;
    
    public RenderCow(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityCow entity) {
        return RenderCow.cowTextures;
    }
    
    static {
        cowTextures = new ResourceLocation("textures/entity/cow/cow.png");
    }
}
