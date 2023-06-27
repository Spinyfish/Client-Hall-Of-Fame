package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderGiantZombie extends RenderLiving<EntityGiantZombie>
{
    private static final ResourceLocation zombieTextures;
    private float scale;
    
    public RenderGiantZombie(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn, final float scaleIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn * scaleIn);
        this.scale = scaleIn;
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerHeldItem(this));
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.field_177189_c = new ModelZombie(0.5f, true);
                this.field_177186_d = new ModelZombie(1.0f, true);
            }
        });
    }
    
    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f);
    }
    
    @Override
    protected void preRenderCallback(final EntityGiantZombie entitylivingbaseIn, final float partialTickTime) {
        GlStateManager.scale(this.scale, this.scale, this.scale);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityGiantZombie entity) {
        return RenderGiantZombie.zombieTextures;
    }
    
    static {
        zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
    }
}
