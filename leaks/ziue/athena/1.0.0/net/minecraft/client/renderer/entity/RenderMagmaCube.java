package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderMagmaCube extends RenderLiving<EntityMagmaCube>
{
    private static final ResourceLocation magmaCubeTextures;
    
    public RenderMagmaCube(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelMagmaCube(), 0.25f);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityMagmaCube entity) {
        return RenderMagmaCube.magmaCubeTextures;
    }
    
    @Override
    protected void preRenderCallback(final EntityMagmaCube entitylivingbaseIn, final float partialTickTime) {
        final int i = entitylivingbaseIn.getSlimeSize();
        final float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (i * 0.5f + 1.0f);
        final float f2 = 1.0f / (f + 1.0f);
        final float f3 = (float)i;
        GlStateManager.scale(f2 * f3, 1.0f / f2 * f3, f2 * f3);
    }
    
    static {
        magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");
    }
}
