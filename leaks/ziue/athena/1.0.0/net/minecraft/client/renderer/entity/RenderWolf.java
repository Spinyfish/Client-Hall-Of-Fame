package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderWolf extends RenderLiving<EntityWolf>
{
    private static final ResourceLocation wolfTextures;
    private static final ResourceLocation tamedWolfTextures;
    private static final ResourceLocation anrgyWolfTextures;
    
    public RenderWolf(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerWolfCollar(this));
    }
    
    @Override
    protected float handleRotationFloat(final EntityWolf livingBase, final float partialTicks) {
        return livingBase.getTailRotation();
    }
    
    @Override
    public void doRender(final EntityWolf entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (entity.isWolfWet()) {
            final float f = entity.getBrightness(partialTicks) * entity.getShadingWhileWet(partialTicks);
            GlStateManager.color(f, f, f);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityWolf entity) {
        return entity.isTamed() ? RenderWolf.tamedWolfTextures : (entity.isAngry() ? RenderWolf.anrgyWolfTextures : RenderWolf.wolfTextures);
    }
    
    static {
        wolfTextures = new ResourceLocation("textures/entity/wolf/wolf.png");
        tamedWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
        anrgyWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_angry.png");
    }
}
