package net.minecraft.client.renderer.entity;

import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.*;

public class RenderCaveSpider extends RenderSpider<EntityCaveSpider>
{
    private static final ResourceLocation caveSpiderTextures;
    
    public RenderCaveSpider(final RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize *= 0.7f;
    }
    
    @Override
    protected void preRenderCallback(final EntityCaveSpider entitylivingbaseIn, final float partialTickTime) {
        GlStateManager.scale(0.7f, 0.7f, 0.7f);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityCaveSpider entity) {
        return RenderCaveSpider.caveSpiderTextures;
    }
    
    static {
        caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");
    }
}
