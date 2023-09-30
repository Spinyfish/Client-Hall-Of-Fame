package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving<EntityGuardian> {
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
    int field_177115_a;

    public RenderGuardian(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelGuardian(), 0.5F);
        this.field_177115_a = ((ModelGuardian) this.mainModel).func_178706_a();
    }

    public boolean shouldRender(final EntityGuardian livingEntity, final ICamera camera, final double camX, final double camY, final double camZ) {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntity.hasTargetedEntity()) {
                final EntityLivingBase entitylivingbase = livingEntity.getTargetedEntity();

                if (entitylivingbase != null) {
                    final Vec3 vec3 = this.func_177110_a(entitylivingbase, (double) entitylivingbase.height * 0.5D, 1.0F);
                    final Vec3 vec31 = this.func_177110_a(livingEntity, livingEntity.getEyeHeight(), 1.0F);

                    return camera.isBoundingBoxInFrustum(AxisAlignedBB.fromBounds(vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord, vec3.zCoord));
                }
            }

            return false;
        }
    }

    private Vec3 func_177110_a(final EntityLivingBase entityLivingBaseIn, final double p_177110_2_, final float p_177110_4_) {
        final double d0 = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * (double) p_177110_4_;
        final double d1 = p_177110_2_ + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * (double) p_177110_4_;
        final double d2 = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * (double) p_177110_4_;
        return new Vec3(d0, d1, d2);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final EntityGuardian entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (this.field_177115_a != ((ModelGuardian) this.mainModel).func_178706_a()) {
            this.mainModel = new ModelGuardian();
            this.field_177115_a = ((ModelGuardian) this.mainModel).func_178706_a();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        final EntityLivingBase entitylivingbase = entity.getTargetedEntity();

        if (entitylivingbase != null) {
            final float f = entity.func_175477_p(partialTicks);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            this.bindTexture(GUARDIAN_BEAM_TEXTURE);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            final float f1 = 240.0F;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f1, f1);
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            final float f2 = (float) entity.worldObj.getTotalWorldTime() + partialTicks;
            final float f3 = f2 * 0.5F % 1.0F;
            final float f4 = entity.getEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x, (float) y + f4, (float) z);
            final Vec3 vec3 = this.func_177110_a(entitylivingbase, (double) entitylivingbase.height * 0.5D, partialTicks);
            final Vec3 vec31 = this.func_177110_a(entity, f4, partialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            final double d0 = vec32.lengthVector() + 1.0D;
            vec32 = vec32.normalize();
            final float f5 = (float) Math.acos(vec32.yCoord);
            final float f6 = (float) Math.atan2(vec32.zCoord, vec32.xCoord);
            GlStateManager.rotate((((float) Math.PI / 2F) + -f6) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f5 * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            final int i = 1;
            final double d1 = (double) f2 * 0.05D * (1.0D - (double) (i & 1) * 2.5D);
            worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
            final float f7 = f * f;
            final int j = 64 + (int) (f7 * 240.0F);
            final int k = 32 + (int) (f7 * 192.0F);
            final int l = 128 - (int) (f7 * 64.0F);
            final double d2 = (double) i * 0.2D;
            final double d3 = d2 * 1.41D;
            final double d4 = 0.0D + Math.cos(d1 + 2.356194490192345D) * d3;
            final double d5 = 0.0D + Math.sin(d1 + 2.356194490192345D) * d3;
            final double d6 = 0.0D + Math.cos(d1 + (Math.PI / 4D)) * d3;
            final double d7 = 0.0D + Math.sin(d1 + (Math.PI / 4D)) * d3;
            final double d8 = 0.0D + Math.cos(d1 + 3.9269908169872414D) * d3;
            final double d9 = 0.0D + Math.sin(d1 + 3.9269908169872414D) * d3;
            final double d10 = 0.0D + Math.cos(d1 + 5.497787143782138D) * d3;
            final double d11 = 0.0D + Math.sin(d1 + 5.497787143782138D) * d3;
            final double d12 = 0.0D + Math.cos(d1 + Math.PI) * d2;
            final double d13 = 0.0D + Math.sin(d1 + Math.PI) * d2;
            final double d14 = 0.0D + Math.cos(d1 + 0.0D) * d2;
            final double d15 = 0.0D + Math.sin(d1 + 0.0D) * d2;
            final double d16 = 0.0D + Math.cos(d1 + (Math.PI / 2D)) * d2;
            final double d17 = 0.0D + Math.sin(d1 + (Math.PI / 2D)) * d2;
            final double d18 = 0.0D + Math.cos(d1 + (Math.PI * 3D / 2D)) * d2;
            final double d19 = 0.0D + Math.sin(d1 + (Math.PI * 3D / 2D)) * d2;
            final double d20 = 0.0D;
            final double d21 = 0.4999D;
            final double d22 = -1.0F + f3;
            final double d23 = d0 * (0.5D / d2) + d22;
            worldrenderer.pos(d12, d0, d13).tex(0.4999D, d23).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d12, 0.0D, d13).tex(0.4999D, d22).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d14, 0.0D, d15).tex(0.0D, d22).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d14, d0, d15).tex(0.0D, d23).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d16, d0, d17).tex(0.4999D, d23).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d16, 0.0D, d17).tex(0.4999D, d22).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d18, 0.0D, d19).tex(0.0D, d22).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d18, d0, d19).tex(0.0D, d23).func_181669_b(j, k, l, 255).endVertex();
            double d24 = 0.0D;

            if (entity.ticksExisted % 2 == 0) {
                d24 = 0.5D;
            }

            worldrenderer.pos(d4, d0, d5).tex(0.5D, d24 + 0.5D).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d6, d0, d7).tex(1.0D, d24 + 0.5D).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d10, d0, d11).tex(1.0D, d24).func_181669_b(j, k, l, 255).endVertex();
            worldrenderer.pos(d8, d0, d9).tex(0.5D, d24).func_181669_b(j, k, l, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityGuardian entitylivingbaseIn, final float partialTickTime) {
        if (entitylivingbaseIn.isElder()) {
            GlStateManager.scale(2.35F, 2.35F, 2.35F);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityGuardian entity) {
        return entity.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
    }
}
