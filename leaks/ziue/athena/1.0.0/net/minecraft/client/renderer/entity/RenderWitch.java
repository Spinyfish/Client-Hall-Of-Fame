package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderWitch extends RenderLiving<EntityWitch>
{
    private static final ResourceLocation witchTextures;
    
    public RenderWitch(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelWitch(0.0f), 0.5f);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerHeldItemWitch(this));
    }
    
    @Override
    public void doRender(final EntityWitch entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        ((ModelWitch)this.mainModel).field_82900_g = (entity.getHeldItem() != null);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityWitch entity) {
        return RenderWitch.witchTextures;
    }
    
    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f);
    }
    
    @Override
    protected void preRenderCallback(final EntityWitch entitylivingbaseIn, final float partialTickTime) {
        final float f = 0.9375f;
        GlStateManager.scale(f, f, f);
    }
    
    static {
        witchTextures = new ResourceLocation("textures/entity/witch.png");
    }
}
