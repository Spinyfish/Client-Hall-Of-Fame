package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;

public class RenderPig extends RenderLiving<EntityPig>
{
    private static final ResourceLocation pigTextures;
    
    public RenderPig(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerSaddle(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityPig entity) {
        return RenderPig.pigTextures;
    }
    
    static {
        pigTextures = new ResourceLocation("textures/entity/pig/pig.png");
    }
}
