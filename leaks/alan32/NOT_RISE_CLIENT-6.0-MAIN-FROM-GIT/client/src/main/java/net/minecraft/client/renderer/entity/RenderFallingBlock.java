package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingBlock> {
    public RenderFallingBlock(final RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final EntityFallingBlock entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (entity.getBlock() != null) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            final IBlockState iblockstate = entity.getBlock();
            final Block block = iblockstate.getBlock();
            final BlockPos blockpos = new BlockPos(entity);
            final World world = entity.getWorldObj();

            if (iblockstate != world.getBlockState(blockpos) && block.getRenderType() != -1) {
                if (block.getRenderType() == 3) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float) x, (float) y, (float) z);
                    GlStateManager.disableLighting();
                    final Tessellator tessellator = Tessellator.getInstance();
                    final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
                    final int i = blockpos.getX();
                    final int j = blockpos.getY();
                    final int k = blockpos.getZ();
                    worldrenderer.setTranslation((float) (-i) - 0.5F, -j, (float) (-k) - 0.5F);
                    final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                    final IBakedModel ibakedmodel = blockrendererdispatcher.getModelFromBlockState(iblockstate, world, null);
                    blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, iblockstate, blockpos, worldrenderer, false);
                    worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
                    tessellator.draw();
                    GlStateManager.enableLighting();
                    GlStateManager.popMatrix();
                    super.doRender(entity, x, y, z, entityYaw, partialTicks);
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityFallingBlock entity) {
        return TextureMap.locationBlocksTexture;
    }
}
