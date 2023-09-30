/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball
extends Render<EntityFireball> {
    private float scale;

    public RenderFireball(RenderManager renderManagerIn, float scaleIn) {
        super(renderManagerIn);
        this.scale = scaleIn;
    }

    @Override
    public void doRender(EntityFireball entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(this.scale, this.scale, this.scale);
        TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMaxU();
        float f2 = textureatlassprite.getMinV();
        float f3 = textureatlassprite.getMaxV();
        float f4 = 1.0f;
        float f5 = 0.5f;
        float f6 = 0.25f;
        GlStateManager.rotate(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.field_181710_j);
        worldrenderer.pos(-0.5, -0.25, 0.0).func_181673_a(f, f3).func_181663_c(0.0f, 1.0f, 0.0f).end();
        worldrenderer.pos(0.5, -0.25, 0.0).func_181673_a(f1, f3).func_181663_c(0.0f, 1.0f, 0.0f).end();
        worldrenderer.pos(0.5, 0.75, 0.0).func_181673_a(f1, f2).func_181663_c(0.0f, 1.0f, 0.0f).end();
        worldrenderer.pos(-0.5, 0.75, 0.0).func_181673_a(f, f2).func_181663_c(0.0f, 1.0f, 0.0f).end();
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFireball entity) {
        return TextureMap.locationBlocksTexture;
    }
}

