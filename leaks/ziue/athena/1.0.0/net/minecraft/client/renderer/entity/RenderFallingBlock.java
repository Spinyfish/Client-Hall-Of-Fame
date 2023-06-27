package net.minecraft.client.renderer.entity;

import net.minecraft.entity.item.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.util.*;

public class RenderFallingBlock extends Render<EntityFallingBlock>
{
    public RenderFallingBlock(final RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5f;
    }
    
    @Override
    public void doRender(final EntityFallingBlock entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (entity.getBlock() != null) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            final IBlockState iblockstate = entity.getBlock();
            final Block block = iblockstate.getBlock();
            final BlockPos blockpos = new BlockPos(entity);
            final World world = entity.getWorldObj();
            if (iblockstate != world.getBlockState(blockpos) && block.getRenderType() != -1 && block.getRenderType() == 3) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)x, (float)y, (float)z);
                GlStateManager.disableLighting();
                final Tessellator tessellator = Tessellator.getInstance();
                final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
                final int i = blockpos.getX();
                final int j = blockpos.getY();
                final int k = blockpos.getZ();
                worldrenderer.setTranslation(-i - 0.5f, -j, -k - 0.5f);
                final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                final IBakedModel ibakedmodel = blockrendererdispatcher.getModelFromBlockState(iblockstate, world, null);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, iblockstate, blockpos, worldrenderer, false);
                worldrenderer.setTranslation(0.0, 0.0, 0.0);
                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityFallingBlock entity) {
        return TextureMap.locationBlocksTexture;
    }
}
