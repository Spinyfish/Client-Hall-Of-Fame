package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.EntityDragon;

import java.util.Random;

public class LayerEnderDragonDeath implements LayerRenderer<EntityDragon> {
    public void doRenderLayer(final EntityDragon entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        if (entitylivingbaseIn.deathTicks > 0) {
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            RenderHelper.disableStandardItemLighting();
            final float f = ((float) entitylivingbaseIn.deathTicks + partialTicks) / 200.0F;
            float f1 = 0.0F;

            if (f > 0.8F) {
                f1 = (f - 0.8F) / 0.2F;
            }

            final Random random = new Random(432L);
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            GlStateManager.disableAlpha();
            GlStateManager.enableCull();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -1.0F, -2.0F);

            for (int i = 0; (float) i < (f + f * f) / 2.0F * 60.0F; ++i) {
                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
                final float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
                final float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
                worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(0.0D, 0.0D, 0.0D).func_181669_b(255, 255, 255, (int) (255.0F * (1.0F - f1))).endVertex();
                worldrenderer.pos(-0.866D * (double) f3, f2, -0.5F * f3).func_181669_b(255, 0, 255, 0).endVertex();
                worldrenderer.pos(0.866D * (double) f3, f2, -0.5F * f3).func_181669_b(255, 0, 255, 0).endVertex();
                worldrenderer.pos(0.0D, f2, 1.0F * f3).func_181669_b(255, 0, 255, 0).endVertex();
                worldrenderer.pos(-0.866D * (double) f3, f2, -0.5F * f3).func_181669_b(255, 0, 255, 0).endVertex();
                tessellator.draw();
            }

            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.shadeModel(7424);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
