package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;

public class RenderPigZombie extends RenderBiped<EntityPigZombie>
{
    private static final ResourceLocation ZOMBIE_PIGMAN_TEXTURE;
    
    public RenderPigZombie(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelZombie(), 0.5f, 1.0f);
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
    protected ResourceLocation getEntityTexture(final EntityPigZombie entity) {
        return RenderPigZombie.ZOMBIE_PIGMAN_TEXTURE;
    }
    
    static {
        ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation("textures/entity/zombie_pigman.png");
    }
}
