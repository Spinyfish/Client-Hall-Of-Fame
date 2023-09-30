package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving<EntitySilverfish> {
    private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

    public RenderSilverfish(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSilverfish(), 0.3F);
    }

    protected float getDeathMaxRotation(final EntitySilverfish entityLivingBaseIn) {
        return 180.0F;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntitySilverfish entity) {
        return silverfishTextures;
    }
}
