package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;

public class RenderSnowMan extends RenderLiving<EntitySnowman>
{
    private static final ResourceLocation snowManTextures;
    
    public RenderSnowMan(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSnowMan(), 0.5f);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerSnowmanHead(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntitySnowman entity) {
        return RenderSnowMan.snowManTextures;
    }
    
    @Override
    public ModelSnowMan getMainModel() {
        return (ModelSnowMan)super.getMainModel();
    }
    
    static {
        snowManTextures = new ResourceLocation("textures/entity/snowman.png");
    }
}
