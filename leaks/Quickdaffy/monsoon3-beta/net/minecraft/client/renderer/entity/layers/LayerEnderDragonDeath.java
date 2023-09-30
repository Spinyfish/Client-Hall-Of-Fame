/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.EntityDragon;

public class LayerEnderDragonDeath
implements LayerRenderer<EntityDragon> {
    @Override
    public void doRenderLayer(EntityDragon entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (entitylivingbaseIn.deathTicks > 0) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            RenderHelper.disableStandardItemLighting();
            float f = ((float)entitylivingbaseIn.deathTicks + partialTicks) / 200.0f;
            float f1 = 0.0f;
            if (f > 0.8f) {
                f1 = (f - 0.8f) / 0.2f;
            }
            Random random = new Random(432L);
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            GlStateManager.disableAlpha();
            GlStateManager.enableCull();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, -1.0f, -2.0f);
            int i = 0;
            while ((float)i < (f + f * f) / 2.0f * 60.0f) {
                GlStateManager.rotate(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f + f * 90.0f, 0.0f, 0.0f, 1.0f);
                float f2 = random.nextFloat() * 20.0f + 5.0f + f1 * 10.0f;
                float f3 = random.nextFloat() * 2.0f + 1.0f + f1 * 2.0f;
                worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0f * (1.0f - f1))).end();
                worldrenderer.pos(-0.866 * (double)f3, f2, -0.5f * f3).color(255, 0, 255, 0).end();
                worldrenderer.pos(0.866 * (double)f3, f2, -0.5f * f3).color(255, 0, 255, 0).end();
                worldrenderer.pos(0.0, f2, 1.0f * f3).color(255, 0, 255, 0).end();
                worldrenderer.pos(-0.866 * (double)f3, f2, -0.5f * f3).color(255, 0, 255, 0).end();
                tessellator.draw();
                ++i;
            }
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.shadeModel(7424);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

