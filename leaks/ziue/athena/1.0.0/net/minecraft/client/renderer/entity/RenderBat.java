package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class RenderBat extends RenderLiving<EntityBat>
{
    private static final ResourceLocation batTextures;
    
    public RenderBat(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBat(), 0.25f);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityBat entity) {
        return RenderBat.batTextures;
    }
    
    @Override
    protected void preRenderCallback(final EntityBat entitylivingbaseIn, final float partialTickTime) {
        GlStateManager.scale(0.35f, 0.35f, 0.35f);
    }
    
    @Override
    protected void rotateCorpse(final EntityBat bat, final float p_77043_2_, final float p_77043_3_, final float partialTicks) {
        if (!bat.getIsBatHanging()) {
            GlStateManager.translate(0.0f, MathHelper.cos(p_77043_2_ * 0.3f) * 0.1f, 0.0f);
        }
        else {
            GlStateManager.translate(0.0f, -0.1f, 0.0f);
        }
        super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
    }
    
    static {
        batTextures = new ResourceLocation("textures/entity/bat.png");
    }
}
