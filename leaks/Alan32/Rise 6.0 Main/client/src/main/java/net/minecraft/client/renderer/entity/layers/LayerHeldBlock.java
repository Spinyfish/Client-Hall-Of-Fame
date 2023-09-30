package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityEnderman;

public class LayerHeldBlock implements LayerRenderer<EntityEnderman> {
    private final RenderEnderman endermanRenderer;

    public LayerHeldBlock(final RenderEnderman endermanRendererIn) {
        this.endermanRenderer = endermanRendererIn;
    }

    public void doRenderLayer(final EntityEnderman entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        final IBlockState iblockstate = entitylivingbaseIn.getHeldBlockState();

        if (iblockstate.getBlock().getMaterial() != Material.air) {
            final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.6875F, -0.75F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.25F, 0.1875F, 0.25F);
            final float f = 0.5F;
            GlStateManager.scale(-f, -f, f);
            final int i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
            final int j = i % 65536;
            final int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.endermanRenderer.bindTexture(TextureMap.locationBlocksTexture);
            blockrendererdispatcher.renderBlockBrightness(iblockstate, 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
