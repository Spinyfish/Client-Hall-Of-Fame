package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.*;
import net.minecraft.client.*;
import net.minecraft.init.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.*;

public class RenderFireball extends Render<EntityFireball>
{
    private float scale;
    
    public RenderFireball(final RenderManager renderManagerIn, final float scaleIn) {
        super(renderManagerIn);
        this.scale = scaleIn;
    }
    
    @Override
    public void doRender(final EntityFireball entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(this.scale, this.scale, this.scale);
        final TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        final float f = textureatlassprite.getMinU();
        final float f2 = textureatlassprite.getMaxU();
        final float f3 = textureatlassprite.getMinV();
        final float f4 = textureatlassprite.getMaxV();
        final float f5 = 1.0f;
        final float f6 = 0.5f;
        final float f7 = 0.25f;
        GlStateManager.rotate(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        worldrenderer.pos(-0.5, -0.25, 0.0).tex(f, f4).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(0.5, -0.25, 0.0).tex(f2, f4).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(0.5, 0.75, 0.0).tex(f2, f3).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(-0.5, 0.75, 0.0).tex(f, f3).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityFireball entity) {
        return TextureMap.locationBlocksTexture;
    }
}
