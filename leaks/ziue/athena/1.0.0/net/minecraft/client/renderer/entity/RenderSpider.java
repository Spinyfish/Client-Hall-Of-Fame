package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;

public class RenderSpider<T extends EntitySpider> extends RenderLiving<T>
{
    private static final ResourceLocation spiderTextures;
    
    public RenderSpider(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSpider(), 1.0f);
        this.addLayer(new LayerSpiderEyes(this));
    }
    
    @Override
    protected float getDeathMaxRotation(final T entityLivingBaseIn) {
        return 180.0f;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final T entity) {
        return RenderSpider.spiderTextures;
    }
    
    static {
        spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");
    }
}
