/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerDeadmau5Head
implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;

    public LayerDeadmau5Head(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (entitylivingbaseIn.getCommandSenderName().equals("deadmau5") && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible()) {
            this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationSkin());
            for (int i = 0; i < 2; ++i) {
                float f = entitylivingbaseIn.prevRotationYaw + (entitylivingbaseIn.rotationYaw - entitylivingbaseIn.prevRotationYaw) * partialTicks - (entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks);
                float f1 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTicks;
                GlStateManager.pushMatrix();
                GlStateManager.rotate(f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(f1, 1.0f, 0.0f, 0.0f);
                GlStateManager.translate(0.375f * (float)(i * 2 - 1), 0.0f, 0.0f);
                GlStateManager.translate(0.0f, -0.375f, 0.0f);
                GlStateManager.rotate(-f1, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(-f, 0.0f, 1.0f, 0.0f);
                float f2 = 1.3333334f;
                GlStateManager.scale(f2, f2, f2);
                this.playerRenderer.getMainModel().renderDeadmau5Head(0.0625f);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}

