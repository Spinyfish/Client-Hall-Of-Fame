package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;

public class RenderGhast extends RenderLiving<EntityGhast> {
    private static final ResourceLocation ghastTextures = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation ghastShootingTextures = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public RenderGhast(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelGhast(), 0.5F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityGhast entity) {
        return entity.isAttacking() ? ghastShootingTextures : ghastTextures;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityGhast entitylivingbaseIn, final float partialTickTime) {
        final float f = 1.0F;
        final float f1 = (8.0F + f) / 2.0F;
        final float f2 = (8.0F + 1.0F / f) / 2.0F;
        GlStateManager.scale(f2, f1, f2);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
