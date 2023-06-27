package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;

public class RenderSheep extends RenderLiving<EntitySheep>
{
    private static final ResourceLocation shearedSheepTextures;
    
    public RenderSheep(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerSheepWool(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntitySheep entity) {
        return RenderSheep.shearedSheepTextures;
    }
    
    static {
        shearedSheepTextures = new ResourceLocation("textures/entity/sheep/sheep.png");
    }
}
