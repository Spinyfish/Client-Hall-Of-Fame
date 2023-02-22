package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;

public abstract class RenderLiving<T extends EntityLiving> extends RendererLivingEntity<T> {
    public RenderLiving(final RenderManager rendermanagerIn, final ModelBase modelbaseIn, final float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }

    protected boolean canRenderName(final T entity) {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }

    public boolean shouldRender(final T livingEntity, final ICamera camera, final double camX, final double camY, final double camZ) {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
            return true;
        } else if (livingEntity.getLeashed() && livingEntity.getLeashedToEntity() != null) {
            final Entity entity = livingEntity.getLeashedToEntity();
            return camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox());
        } else {
            return false;
        }
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
    }

    public void func_177105_a(final T entityLivingIn, final float partialTicks) {
        final int i = entityLivingIn.getBrightnessForRender(partialTicks);
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
    }

    /**
     * Gets the value between start and end according to pct
     */
    private double interpolateValue(final double start, final double end, final double pct) {
        return start + (end - start) * pct;
    }

    protected void renderLeash(final T entityLivingIn, double x, double y, double z, final float entityYaw, final float partialTicks) {
        if (!Config.isShaders() || !Shaders.isShadowPass) {
            final Entity entity = entityLivingIn.getLeashedToEntity();

            if (entity != null) {
                y = y - (1.6D - (double) entityLivingIn.height) * 0.5D;
                final Tessellator tessellator = Tessellator.getInstance();
                final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                final double d0 = this.interpolateValue(entity.prevRotationYaw, entity.rotationYaw, partialTicks * 0.5F) * 0.01745329238474369D;
                final double d1 = this.interpolateValue(entity.prevRotationPitch, entity.rotationPitch, partialTicks * 0.5F) * 0.01745329238474369D;
                double d2 = Math.cos(d0);
                double d3 = Math.sin(d0);
                double d4 = Math.sin(d1);

                if (entity instanceof EntityHanging) {
                    d2 = 0.0D;
                    d3 = 0.0D;
                    d4 = -1.0D;
                }

                final double d5 = Math.cos(d1);
                final double d6 = this.interpolateValue(entity.prevPosX, entity.posX, partialTicks) - d2 * 0.7D - d3 * 0.5D * d5;
                final double d7 = this.interpolateValue(entity.prevPosY + (double) entity.getEyeHeight() * 0.7D, entity.posY + (double) entity.getEyeHeight() * 0.7D, partialTicks) - d4 * 0.5D - 0.25D;
                final double d8 = this.interpolateValue(entity.prevPosZ, entity.posZ, partialTicks) - d3 * 0.7D + d2 * 0.5D * d5;
                final double d9 = this.interpolateValue(entityLivingIn.prevRenderYawOffset, entityLivingIn.renderYawOffset, partialTicks) * 0.01745329238474369D + (Math.PI / 2D);
                d2 = Math.cos(d9) * (double) entityLivingIn.width * 0.4D;
                d3 = Math.sin(d9) * (double) entityLivingIn.width * 0.4D;
                final double d10 = this.interpolateValue(entityLivingIn.prevPosX, entityLivingIn.posX, partialTicks) + d2;
                final double d11 = this.interpolateValue(entityLivingIn.prevPosY, entityLivingIn.posY, partialTicks);
                final double d12 = this.interpolateValue(entityLivingIn.prevPosZ, entityLivingIn.posZ, partialTicks) + d3;
                x = x + d2;
                z = z + d3;
                final double d13 = (float) (d6 - d10);
                final double d14 = (float) (d7 - d11);
                final double d15 = (float) (d8 - d12);
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableCull();

                if (Config.isShaders()) {
                    Shaders.beginLeash();
                }

                final int i = 24;
                final double d16 = 0.025D;
                worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

                for (int j = 0; j <= 24; ++j) {
                    float f = 0.5F;
                    float f1 = 0.4F;
                    float f2 = 0.3F;

                    if (j % 2 == 0) {
                        f *= 0.7F;
                        f1 *= 0.7F;
                        f2 *= 0.7F;
                    }

                    final float f3 = (float) j / 24.0F;
                    worldrenderer.pos(x + d13 * (double) f3 + 0.0D, y + d14 * (double) (f3 * f3 + f3) * 0.5D + (double) ((24.0F - (float) j) / 18.0F + 0.125F), z + d15 * (double) f3).func_181666_a(f, f1, f2, 1.0F).endVertex();
                    worldrenderer.pos(x + d13 * (double) f3 + 0.025D, y + d14 * (double) (f3 * f3 + f3) * 0.5D + (double) ((24.0F - (float) j) / 18.0F + 0.125F) + 0.025D, z + d15 * (double) f3).func_181666_a(f, f1, f2, 1.0F).endVertex();
                }

                tessellator.draw();
                worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

                for (int k = 0; k <= 24; ++k) {
                    float f4 = 0.5F;
                    float f5 = 0.4F;
                    float f6 = 0.3F;

                    if (k % 2 == 0) {
                        f4 *= 0.7F;
                        f5 *= 0.7F;
                        f6 *= 0.7F;
                    }

                    final float f7 = (float) k / 24.0F;
                    worldrenderer.pos(x + d13 * (double) f7 + 0.0D, y + d14 * (double) (f7 * f7 + f7) * 0.5D + (double) ((24.0F - (float) k) / 18.0F + 0.125F) + 0.025D, z + d15 * (double) f7).func_181666_a(f4, f5, f6, 1.0F).endVertex();
                    worldrenderer.pos(x + d13 * (double) f7 + 0.025D, y + d14 * (double) (f7 * f7 + f7) * 0.5D + (double) ((24.0F - (float) k) / 18.0F + 0.125F), z + d15 * (double) f7 + 0.025D).func_181666_a(f4, f5, f6, 1.0F).endVertex();
                }

                tessellator.draw();

                if (Config.isShaders()) {
                    Shaders.endLeash();
                }

                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                GlStateManager.enableCull();
            }
        }
    }
}
