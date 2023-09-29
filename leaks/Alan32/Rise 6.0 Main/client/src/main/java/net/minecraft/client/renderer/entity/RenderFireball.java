package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball extends Render<EntityFireball> {
    private final float scale;

    public RenderFireball(final RenderManager renderManagerIn, final float scaleIn) {
        super(renderManagerIn);
        this.scale = scaleIn;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final EntityFireball entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(this.scale, this.scale, this.scale);
        final TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        final float f = textureatlassprite.getMinU();
        final float f1 = textureatlassprite.getMaxU();
        final float f2 = textureatlassprite.getMinV();
        final float f3 = textureatlassprite.getMaxV();
        final float f4 = 1.0F;
        final float f5 = 0.5F;
        final float f6 = 0.25F;
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.field_181710_j);
        worldrenderer.pos(-0.5D, -0.25D, 0.0D).tex(f, f3).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(0.5D, -0.25D, 0.0D).tex(f1, f3).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(0.5D, 0.75D, 0.0D).tex(f1, f2).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(-0.5D, 0.75D, 0.0D).tex(f, f2).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityFireball entity) {
        return TextureMap.locationBlocksTexture;
    }
}
