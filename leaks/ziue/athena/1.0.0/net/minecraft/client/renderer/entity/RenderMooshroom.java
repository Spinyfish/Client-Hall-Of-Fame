package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;

public class RenderMooshroom extends RenderLiving<EntityMooshroom>
{
    private static final ResourceLocation mooshroomTextures;
    
    public RenderMooshroom(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerMooshroomMushroom(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityMooshroom entity) {
        return RenderMooshroom.mooshroomTextures;
    }
    
    static {
        mooshroomTextures = new ResourceLocation("textures/entity/cow/mooshroom.png");
    }
}
