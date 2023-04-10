package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render
{
    private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    public RenderPainting(RenderManager renderManagerIn)
    {
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
    public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);
        EntityPainting.EnumArt var10 = entity.art;
        float var11 = 0.0625F;
        GlStateManager.scale(var11, var11, var11);
        this.renderPainting(entity, var10.sizeX, var10.sizeY, var10.offsetX, var10.offsetY);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getPaintingTexture(EntityPainting painting)
    {
        return KRISTOFFER_PAINTING_TEXTURE;
    }

    private void renderPainting(EntityPainting painting, int width, int height, int textureU, int textureV)
    {
        float var6 = (float)(-width) / 2.0F;
        float var7 = (float)(-height) / 2.0F;
        float var8 = 0.5F;
        float var9 = 0.75F;
        float var10 = 0.8125F;
        float var11 = 0.0F;
        float var12 = 0.0625F;
        float var13 = 0.75F;
        float var14 = 0.8125F;
        float var15 = 0.001953125F;
        float var16 = 0.001953125F;
        float var17 = 0.7519531F;
        float var18 = 0.7519531F;
        float var19 = 0.0F;
        float var20 = 0.0625F;

        for (int var21 = 0; var21 < width / 16; ++var21)
        {
            for (int var22 = 0; var22 < height / 16; ++var22)
            {
                float var23 = var6 + (float)((var21 + 1) * 16);
                float var24 = var6 + (float)(var21 * 16);
                float var25 = var7 + (float)((var22 + 1) * 16);
                float var26 = var7 + (float)(var22 * 16);
                this.setLightmap(painting, (var23 + var24) / 2.0F, (var25 + var26) / 2.0F);
                float var27 = (float)(textureU + width - var21 * 16) / 256.0F;
                float var28 = (float)(textureU + width - (var21 + 1) * 16) / 256.0F;
                float var29 = (float)(textureV + height - var22 * 16) / 256.0F;
                float var30 = (float)(textureV + height - (var22 + 1) * 16) / 256.0F;
                Tessellator var31 = Tessellator.getInstance();
                WorldRenderer var32 = var31.getWorldRenderer();
                var32.startDrawingQuads();
                var32.setNormal(0.0F, 0.0F, -1.0F);
                var32.addVertexWithUV((double)var23, (double)var26, (double)(-var8), (double)var28, (double)var29);
                var32.addVertexWithUV((double)var24, (double)var26, (double)(-var8), (double)var27, (double)var29);
                var32.addVertexWithUV((double)var24, (double)var25, (double)(-var8), (double)var27, (double)var30);
                var32.addVertexWithUV((double)var23, (double)var25, (double)(-var8), (double)var28, (double)var30);
                var32.setNormal(0.0F, 0.0F, 1.0F);
                var32.addVertexWithUV((double)var23, (double)var25, (double)var8, (double)var9, (double)var11);
                var32.addVertexWithUV((double)var24, (double)var25, (double)var8, (double)var10, (double)var11);
                var32.addVertexWithUV((double)var24, (double)var26, (double)var8, (double)var10, (double)var12);
                var32.addVertexWithUV((double)var23, (double)var26, (double)var8, (double)var9, (double)var12);
                var32.setNormal(0.0F, 1.0F, 0.0F);
                var32.addVertexWithUV((double)var23, (double)var25, (double)(-var8), (double)var13, (double)var15);
                var32.addVertexWithUV((double)var24, (double)var25, (double)(-var8), (double)var14, (double)var15);
                var32.addVertexWithUV((double)var24, (double)var25, (double)var8, (double)var14, (double)var16);
                var32.addVertexWithUV((double)var23, (double)var25, (double)var8, (double)var13, (double)var16);
                var32.setNormal(0.0F, -1.0F, 0.0F);
                var32.addVertexWithUV((double)var23, (double)var26, (double)var8, (double)var13, (double)var15);
                var32.addVertexWithUV((double)var24, (double)var26, (double)var8, (double)var14, (double)var15);
                var32.addVertexWithUV((double)var24, (double)var26, (double)(-var8), (double)var14, (double)var16);
                var32.addVertexWithUV((double)var23, (double)var26, (double)(-var8), (double)var13, (double)var16);
                var32.setNormal(-1.0F, 0.0F, 0.0F);
                var32.addVertexWithUV((double)var23, (double)var25, (double)var8, (double)var18, (double)var19);
                var32.addVertexWithUV((double)var23, (double)var26, (double)var8, (double)var18, (double)var20);
                var32.addVertexWithUV((double)var23, (double)var26, (double)(-var8), (double)var17, (double)var20);
                var32.addVertexWithUV((double)var23, (double)var25, (double)(-var8), (double)var17, (double)var19);
                var32.setNormal(1.0F, 0.0F, 0.0F);
                var32.addVertexWithUV((double)var24, (double)var25, (double)(-var8), (double)var18, (double)var19);
                var32.addVertexWithUV((double)var24, (double)var26, (double)(-var8), (double)var18, (double)var20);
                var32.addVertexWithUV((double)var24, (double)var26, (double)var8, (double)var17, (double)var20);
                var32.addVertexWithUV((double)var24, (double)var25, (double)var8, (double)var17, (double)var19);
                var31.draw();
            }
        }
    }

    private void setLightmap(EntityPainting painting, float p_77008_2_, float p_77008_3_)
    {
        int var4 = MathHelper.floor_double(painting.posX);
        int var5 = MathHelper.floor_double(painting.posY + (double)(p_77008_3_ / 16.0F));
        int var6 = MathHelper.floor_double(painting.posZ);
        EnumFacing var7 = painting.facingDirection;

        if (var7 == EnumFacing.NORTH)
        {
            var4 = MathHelper.floor_double(painting.posX + (double)(p_77008_2_ / 16.0F));
        }

        if (var7 == EnumFacing.WEST)
        {
            var6 = MathHelper.floor_double(painting.posZ - (double)(p_77008_2_ / 16.0F));
        }

        if (var7 == EnumFacing.SOUTH)
        {
            var4 = MathHelper.floor_double(painting.posX - (double)(p_77008_2_ / 16.0F));
        }

        if (var7 == EnumFacing.EAST)
        {
            var6 = MathHelper.floor_double(painting.posZ + (double)(p_77008_2_ / 16.0F));
        }

        int var8 = this.renderManager.worldObj.getCombinedLight(new BlockPos(var4, var5, var6), 0);
        int var9 = var8 % 65536;
        int var10 = var8 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var9, (float)var10);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getPaintingTexture((EntityPainting)entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *  
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityPainting)entity, x, y, z, entityYaw, partialTicks);
    }
}
