package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;
import shadersmod.client.Shaders;

public abstract class RenderLiving extends RendererLivingEntity
{

    public RenderLiving(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn)
    {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }

    /**
     * Test if the entity name must be rendered
     */
    protected boolean canRenderName(EntityLiving targetEntity)
    {
        return super.canRenderName(targetEntity) && (targetEntity.getAlwaysRenderNameTagForRender() || targetEntity.hasCustomName() && targetEntity == this.renderManager.pointedEntity);
    }

    public boolean shouldRenderLiving(EntityLiving entityLivingIn, ICamera camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(entityLivingIn, camera, camX, camY, camZ))
        {
            return true;
        }
        else if (entityLivingIn.getLeashed() && entityLivingIn.getLeashedToEntity() != null)
        {
            Entity var9 = entityLivingIn.getLeashedToEntity();
            return camera.isBoundingBoxInFrustum(var9.getEntityBoundingBox());
        }
        else
        {
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
    public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender((EntityLivingBase)entity, x, y, z, entityYaw, partialTicks);
        this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
    }

    public void func_177105_a(EntityLiving entityLivingIn, float partialTicks)
    {
        int var3 = entityLivingIn.getBrightnessForRender(partialTicks);
        int var4 = var3 % 65536;
        int var5 = var3 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var4 / 1.0F, (float)var5 / 1.0F);
    }

    /**
     * Gets the value between start and end according to pct
     */
    private double interpolateValue(double start, double end, double pct)
    {
        return start + (end - start) * pct;
    }

    protected void renderLeash(EntityLiving entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (!Config.isShaders() || !Shaders.isShadowPass)
        {
            Entity var10 = entityLivingIn.getLeashedToEntity();

            if (var10 != null)
            {
                y -= (1.6D - (double)entityLivingIn.height) * 0.5D;
                Tessellator var11 = Tessellator.getInstance();
                WorldRenderer var12 = var11.getWorldRenderer();
                double var13 = this.interpolateValue((double)var10.prevRotationYaw, (double)var10.rotationYaw, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
                double var15 = this.interpolateValue((double)var10.prevRotationPitch, (double)var10.rotationPitch, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
                double var17 = Math.cos(var13);
                double var19 = Math.sin(var13);
                double var21 = Math.sin(var15);

                if (var10 instanceof EntityHanging)
                {
                    var17 = 0.0D;
                    var19 = 0.0D;
                    var21 = -1.0D;
                }

                double var23 = Math.cos(var15);
                double var25 = this.interpolateValue(var10.prevPosX, var10.posX, (double)partialTicks) - var17 * 0.7D - var19 * 0.5D * var23;
                double var27 = this.interpolateValue(var10.prevPosY + (double)var10.getEyeHeight() * 0.7D, var10.posY + (double)var10.getEyeHeight() * 0.7D, (double)partialTicks) - var21 * 0.5D - 0.25D;
                double var29 = this.interpolateValue(var10.prevPosZ, var10.posZ, (double)partialTicks) - var19 * 0.7D + var17 * 0.5D * var23;
                double var31 = this.interpolateValue((double)entityLivingIn.prevRenderYawOffset, (double)entityLivingIn.renderYawOffset, (double)partialTicks) * 0.01745329238474369D + (Math.PI / 2D);
                var17 = Math.cos(var31) * (double)entityLivingIn.width * 0.4D;
                var19 = Math.sin(var31) * (double)entityLivingIn.width * 0.4D;
                double var33 = this.interpolateValue(entityLivingIn.prevPosX, entityLivingIn.posX, (double)partialTicks) + var17;
                double var35 = this.interpolateValue(entityLivingIn.prevPosY, entityLivingIn.posY, (double)partialTicks);
                double var37 = this.interpolateValue(entityLivingIn.prevPosZ, entityLivingIn.posZ, (double)partialTicks) + var19;
                x += var17;
                z += var19;
                double var39 = (double)((float)(var25 - var33));
                double var41 = (double)((float)(var27 - var35));
                double var43 = (double)((float)(var29 - var37));
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableCull();

                if (Config.isShaders())
                {
                    Shaders.beginLeash();
                }

                boolean var45 = true;
                double var46 = 0.025D;
                var12.startDrawing(5);
                int var48;
                float var49;

                for (var48 = 0; var48 <= 24; ++var48)
                {
                    if (var48 % 2 == 0)
                    {
                        var12.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                    }
                    else
                    {
                        var12.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
                    }

                    var49 = (float)var48 / 24.0F;
                    var12.addVertex(x + var39 * (double)var49 + 0.0D, y + var41 * (double)(var49 * var49 + var49) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F), z + var43 * (double)var49);
                    var12.addVertex(x + var39 * (double)var49 + 0.025D, y + var41 * (double)(var49 * var49 + var49) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025D, z + var43 * (double)var49);
                }

                var11.draw();
                var12.startDrawing(5);

                for (var48 = 0; var48 <= 24; ++var48)
                {
                    if (var48 % 2 == 0)
                    {
                        var12.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                    }
                    else
                    {
                        var12.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
                    }

                    var49 = (float)var48 / 24.0F;
                    var12.addVertex(x + var39 * (double)var49 + 0.0D, y + var41 * (double)(var49 * var49 + var49) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025D, z + var43 * (double)var49);
                    var12.addVertex(x + var39 * (double)var49 + 0.025D, y + var41 * (double)(var49 * var49 + var49) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F), z + var43 * (double)var49 + 0.025D);
                }

                var11.draw();

                if (Config.isShaders())
                {
                    Shaders.endLeash();
                }

                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                GlStateManager.enableCull();
            }
        }
    }

    /**
     * Test if the entity name must be rendered
     */
    protected boolean canRenderName(EntityLivingBase targetEntity)
    {
        return this.canRenderName((EntityLiving)targetEntity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *  
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityLiving)entity, x, y, z, entityYaw, partialTicks);
    }

    protected boolean canRenderName(Entity entity)
    {
        return this.canRenderName((EntityLiving)entity);
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
        this.doRender((EntityLiving)entity, x, y, z, entityYaw, partialTicks);
    }

    public boolean shouldRender(Entity livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        return this.shouldRenderLiving((EntityLiving)livingEntity, camera, camX, camY, camZ);
    }
}
