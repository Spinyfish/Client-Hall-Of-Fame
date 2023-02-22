package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting> {
    private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    public RenderPainting(final RenderManager renderManagerIn) {
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
    public void doRender(final EntityPainting entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);
        final EntityPainting.EnumArt entitypainting$enumart = entity.art;
        final float f = 0.0625F;
        GlStateManager.scale(f, f, f);
        this.renderPainting(entity, entitypainting$enumart.sizeX, entitypainting$enumart.sizeY, entitypainting$enumart.offsetX, entitypainting$enumart.offsetY);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityPainting entity) {
        return KRISTOFFER_PAINTING_TEXTURE;
    }

    private void renderPainting(final EntityPainting painting, final int width, final int height, final int textureU, final int textureV) {
        final float f = (float) (-width) / 2.0F;
        final float f1 = (float) (-height) / 2.0F;
        final float f2 = 0.5F;
        final float f3 = 0.75F;
        final float f4 = 0.8125F;
        final float f5 = 0.0F;
        final float f6 = 0.0625F;
        final float f7 = 0.75F;
        final float f8 = 0.8125F;
        final float f9 = 0.001953125F;
        final float f10 = 0.001953125F;
        final float f11 = 0.7519531F;
        final float f12 = 0.7519531F;
        final float f13 = 0.0F;
        final float f14 = 0.0625F;

        for (int i = 0; i < width / 16; ++i) {
            for (int j = 0; j < height / 16; ++j) {
                final float f15 = f + (float) ((i + 1) * 16);
                final float f16 = f + (float) (i * 16);
                final float f17 = f1 + (float) ((j + 1) * 16);
                final float f18 = f1 + (float) (j * 16);
                this.setLightmap(painting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
                final float f19 = (float) (textureU + width - i * 16) / 256.0F;
                final float f20 = (float) (textureU + width - (i + 1) * 16) / 256.0F;
                final float f21 = (float) (textureV + height - j * 16) / 256.0F;
                final float f22 = (float) (textureV + height - (j + 1) * 16) / 256.0F;
                final Tessellator tessellator = Tessellator.getInstance();
                final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(7, DefaultVertexFormats.field_181710_j);
                worldrenderer.pos(f15, f18, -f2).tex(f20, f21).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f16, f18, -f2).tex(f19, f21).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f16, f17, -f2).tex(f19, f22).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f15, f17, -f2).tex(f20, f22).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f15, f17, f2).tex(f3, f5).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f16, f17, f2).tex(f4, f5).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f16, f18, f2).tex(f4, f6).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f15, f18, f2).tex(f3, f6).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f15, f17, -f2).tex(f7, f9).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, -f2).tex(f8, f9).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, f2).tex(f8, f10).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f17, f2).tex(f7, f10).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, f2).tex(f7, f9).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, f2).tex(f8, f9).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, -f2).tex(f8, f10).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, -f2).tex(f7, f10).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f17, f2).tex(f12, f13).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, f2).tex(f12, f14).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, -f2).tex(f11, f14).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f17, -f2).tex(f11, f13).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, -f2).tex(f12, f13).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, -f2).tex(f12, f14).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, f2).tex(f11, f14).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, f2).tex(f11, f13).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
                tessellator.draw();
            }
        }
    }

    private void setLightmap(final EntityPainting painting, final float p_77008_2_, final float p_77008_3_) {
        int i = MathHelper.floor_double(painting.posX);
        final int j = MathHelper.floor_double(painting.posY + (double) (p_77008_3_ / 16.0F));
        int k = MathHelper.floor_double(painting.posZ);
        final EnumFacing enumfacing = painting.facingDirection;

        if (enumfacing == EnumFacing.NORTH) {
            i = MathHelper.floor_double(painting.posX + (double) (p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.WEST) {
            k = MathHelper.floor_double(painting.posZ - (double) (p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.SOUTH) {
            i = MathHelper.floor_double(painting.posX - (double) (p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.EAST) {
            k = MathHelper.floor_double(painting.posZ + (double) (p_77008_2_ / 16.0F));
        }

        final int l = this.renderManager.worldObj.getCombinedLight(new BlockPos(i, j, k), 0);
        final int i1 = l % 65536;
        final int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) i1, (float) j1);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }
}
