package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render<EntityFishHook> {
    private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

    public RenderFish(final RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final EntityFishHook entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        this.bindEntityTexture(entity);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        final int i = 1;
        final int j = 2;
        final float f = 0.0625F;
        final float f1 = 0.125F;
        final float f2 = 0.125F;
        final float f3 = 0.1875F;
        final float f4 = 1.0F;
        final float f5 = 0.5F;
        final float f6 = 0.5F;
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.field_181710_j);
        worldrenderer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        if (entity.angler != null) {
            final float f7 = entity.angler.getSwingProgress(partialTicks);
            final float f8 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI);
            Vec3 vec3 = new Vec3(-0.36D, 0.03D, 0.35D);
            vec3 = vec3.rotatePitch(-(entity.angler.prevRotationPitch + (entity.angler.rotationPitch - entity.angler.prevRotationPitch) * partialTicks) * (float) Math.PI / 180.0F);
            vec3 = vec3.rotateYaw(-(entity.angler.prevRotationYaw + (entity.angler.rotationYaw - entity.angler.prevRotationYaw) * partialTicks) * (float) Math.PI / 180.0F);
            vec3 = vec3.rotateYaw(f8 * 0.5F);
            vec3 = vec3.rotatePitch(-f8 * 0.7F);
            double d0 = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * (double) partialTicks + vec3.xCoord;
            double d1 = entity.angler.prevPosY + (entity.angler.posY - entity.angler.prevPosY) * (double) partialTicks + vec3.yCoord;
            double d2 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * (double) partialTicks + vec3.zCoord;
            double d3 = entity.angler.getEyeHeight();

            if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || entity.angler != Minecraft.getMinecraft().thePlayer) {
                final float f9 = (entity.angler.prevRenderYawOffset + (entity.angler.renderYawOffset - entity.angler.prevRenderYawOffset) * partialTicks) * (float) Math.PI / 180.0F;
                final double d4 = MathHelper.sin(f9);
                final double d6 = MathHelper.cos(f9);
                final double d8 = 0.35D;
                final double d10 = 0.8D;
                d0 = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * (double) partialTicks - d6 * 0.35D - d4 * 0.8D;
                d1 = entity.angler.prevPosY + d3 + (entity.angler.posY - entity.angler.prevPosY) * (double) partialTicks - 0.45D;
                d2 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * (double) partialTicks - d4 * 0.35D + d6 * 0.8D;
                d3 = entity.angler.isSneaking() ? -0.1875D : 0.0D;
            }

            final double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
            final double d5 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + 0.25D;
            final double d7 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
            final double d9 = (float) (d0 - d13);
            final double d11 = (double) ((float) (d1 - d5)) + d3;
            final double d12 = (float) (d2 - d7);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            final int k = 16;

            for (int l = 0; l <= 16; ++l) {
                final float f10 = (float) l / 16.0F;
                worldrenderer.pos(x + d9 * (double) f10, y + d11 * (double) (f10 * f10 + f10) * 0.5D + 0.25D, z + d12 * (double) f10).func_181669_b(0, 0, 0, 255).endVertex();
            }

            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityFishHook entity) {
        return FISH_PARTICLES;
    }
}
