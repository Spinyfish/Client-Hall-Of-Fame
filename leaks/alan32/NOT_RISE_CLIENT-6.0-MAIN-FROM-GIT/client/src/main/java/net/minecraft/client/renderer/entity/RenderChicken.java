package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving<EntityChicken> {
    private static final ResourceLocation chickenTextures = new ResourceLocation("textures/entity/chicken.png");

    public RenderChicken(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityChicken entity) {
        return chickenTextures;
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(final EntityChicken livingBase, final float partialTicks) {
        final float f = livingBase.field_70888_h + (livingBase.wingRotation - livingBase.field_70888_h) * partialTicks;
        final float f1 = livingBase.field_70884_g + (livingBase.destPos - livingBase.field_70884_g) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
