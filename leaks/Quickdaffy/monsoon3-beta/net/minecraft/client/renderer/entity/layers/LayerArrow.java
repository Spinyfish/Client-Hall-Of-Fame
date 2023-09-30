/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

public class LayerArrow
implements LayerRenderer<EntityLivingBase> {
    private final RendererLivingEntity field_177168_a;

    public LayerArrow(RendererLivingEntity p_i46124_1_) {
        this.field_177168_a = p_i46124_1_;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        int i = entitylivingbaseIn.getArrowCountInEntity();
        if (i > 0) {
            EntityArrow entity = new EntityArrow(entitylivingbaseIn.worldObj, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            Random random = new Random(entitylivingbaseIn.getEntityId());
            RenderHelper.disableStandardItemLighting();
            for (int j = 0; j < i; ++j) {
                GlStateManager.pushMatrix();
                ModelRenderer modelrenderer = this.field_177168_a.getMainModel().getRandomModelBox(random);
                ModelBox modelbox = modelrenderer.cubeList.get(random.nextInt(modelrenderer.cubeList.size()));
                modelrenderer.postRender(0.0625f);
                float f = random.nextFloat();
                float f1 = random.nextFloat();
                float f2 = random.nextFloat();
                float f3 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f) / 16.0f;
                float f4 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f1) / 16.0f;
                float f5 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f2) / 16.0f;
                GlStateManager.translate(f3, f4, f5);
                f = f * 2.0f - 1.0f;
                f1 = f1 * 2.0f - 1.0f;
                f2 = f2 * 2.0f - 1.0f;
                float f6 = MathHelper.sqrt_float((f *= -1.0f) * f + (f2 *= -1.0f) * f2);
                entity.prevRotationYaw = entity.rotationYaw = (float)(Math.atan2(f, f2) * 180.0 / Math.PI);
                entity.prevRotationPitch = entity.rotationPitch = (float)(Math.atan2(f1 *= -1.0f, f6) * 180.0 / Math.PI);
                double d0 = 0.0;
                double d1 = 0.0;
                double d2 = 0.0;
                this.field_177168_a.getRenderManager().renderEntityWithPosYaw(entity, d0, d1, d2, 0.0f, partialTicks);
                GlStateManager.popMatrix();
            }
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

