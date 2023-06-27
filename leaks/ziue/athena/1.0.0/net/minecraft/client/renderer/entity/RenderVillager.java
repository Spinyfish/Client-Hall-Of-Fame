package net.minecraft.client.renderer.entity;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderVillager extends RenderLiving<EntityVillager>
{
    private static final ResourceLocation villagerTextures;
    private static final ResourceLocation farmerVillagerTextures;
    private static final ResourceLocation librarianVillagerTextures;
    private static final ResourceLocation priestVillagerTextures;
    private static final ResourceLocation smithVillagerTextures;
    private static final ResourceLocation butcherVillagerTextures;
    
    public RenderVillager(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelVillager(0.0f), 0.5f);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
    }
    
    @Override
    public ModelVillager getMainModel() {
        return (ModelVillager)super.getMainModel();
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityVillager entity) {
        switch (entity.getProfession()) {
            case 0: {
                return RenderVillager.farmerVillagerTextures;
            }
            case 1: {
                return RenderVillager.librarianVillagerTextures;
            }
            case 2: {
                return RenderVillager.priestVillagerTextures;
            }
            case 3: {
                return RenderVillager.smithVillagerTextures;
            }
            case 4: {
                return RenderVillager.butcherVillagerTextures;
            }
            default: {
                return RenderVillager.villagerTextures;
            }
        }
    }
    
    @Override
    protected void preRenderCallback(final EntityVillager entitylivingbaseIn, final float partialTickTime) {
        float f = 0.9375f;
        if (entitylivingbaseIn.getGrowingAge() < 0) {
            f *= 0.5;
            this.shadowSize = 0.25f;
        }
        else {
            this.shadowSize = 0.5f;
        }
        GlStateManager.scale(f, f, f);
    }
    
    static {
        villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
        farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png");
        librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");
        priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png");
        smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png");
        butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");
    }
}
