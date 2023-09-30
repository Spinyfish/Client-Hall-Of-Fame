/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVillager
extends RenderLiving<EntityVillager> {
    private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
    private static final ResourceLocation farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png");
    private static final ResourceLocation librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");
    private static final ResourceLocation priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png");
    private static final ResourceLocation smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png");
    private static final ResourceLocation butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");

    public RenderVillager(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelVillager(0.0f), 0.5f);
        this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
    }

    @Override
    public ModelVillager getMainModel() {
        return (ModelVillager)super.getMainModel();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVillager entity) {
        switch (entity.getProfession()) {
            case 0: {
                return farmerVillagerTextures;
            }
            case 1: {
                return librarianVillagerTextures;
            }
            case 2: {
                return priestVillagerTextures;
            }
            case 3: {
                return smithVillagerTextures;
            }
            case 4: {
                return butcherVillagerTextures;
            }
        }
        return villagerTextures;
    }

    @Override
    protected void preRenderCallback(EntityVillager entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375f;
        if (entitylivingbaseIn.getGrowingAge() < 0) {
            f = (float)((double)f * 0.5);
            this.shadowSize = 0.25f;
        } else {
            this.shadowSize = 0.5f;
        }
        GlStateManager.scale(f, f, f);
    }
}

