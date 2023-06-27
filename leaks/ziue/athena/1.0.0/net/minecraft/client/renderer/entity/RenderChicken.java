package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.client.model.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class RenderChicken extends RenderLiving<EntityChicken>
{
    private static final ResourceLocation chickenTextures;
    
    public RenderChicken(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityChicken entity) {
        return RenderChicken.chickenTextures;
    }
    
    @Override
    protected float handleRotationFloat(final EntityChicken livingBase, final float partialTicks) {
        final float f = livingBase.field_70888_h + (livingBase.wingRotation - livingBase.field_70888_h) * partialTicks;
        final float f2 = livingBase.field_70884_g + (livingBase.destPos - livingBase.field_70884_g) * partialTicks;
        return (MathHelper.sin(f) + 1.0f) * f2;
    }
    
    static {
        chickenTextures = new ResourceLocation("textures/entity/chicken.png");
    }
}
