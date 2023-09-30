/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerMooshroomMushroom
implements LayerRenderer<EntityMooshroom> {
    private final RenderMooshroom mooshroomRenderer;
    private ModelRenderer modelRendererMushroom;
    private static final ResourceLocation LOCATION_MUSHROOM_RED = new ResourceLocation("textures/entity/cow/mushroom_red.png");
    private static boolean hasTextureMushroom = false;

    public static void update() {
        hasTextureMushroom = Config.hasResource(LOCATION_MUSHROOM_RED);
    }

    public LayerMooshroomMushroom(RenderMooshroom mooshroomRendererIn) {
        this.mooshroomRenderer = mooshroomRendererIn;
        this.modelRendererMushroom = new ModelRenderer(this.mooshroomRenderer.mainModel);
        this.modelRendererMushroom.setTextureSize(16, 16);
        this.modelRendererMushroom.rotationPointX = -6.0f;
        this.modelRendererMushroom.rotationPointZ = -8.0f;
        this.modelRendererMushroom.rotateAngleY = MathHelper.PI / 4.0f;
        int[][] aint = new int[][]{null, null, {16, 16, 0, 0}, {16, 16, 0, 0}, null, null};
        this.modelRendererMushroom.addBox(aint, 0.0f, 0.0f, 10.0f, 20.0f, 16.0f, 0.0f, 0.0f);
        int[][] aint1 = new int[][]{null, null, null, null, {16, 16, 0, 0}, {16, 16, 0, 0}};
        this.modelRendererMushroom.addBox(aint1, 10.0f, 0.0f, 0.0f, 0.0f, 16.0f, 20.0f, 0.0f);
    }

    @Override
    public void doRenderLayer(EntityMooshroom entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible()) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            if (hasTextureMushroom) {
                this.mooshroomRenderer.bindTexture(LOCATION_MUSHROOM_RED);
            } else {
                this.mooshroomRenderer.bindTexture(TextureMap.locationBlocksTexture);
            }
            GlStateManager.enableCull();
            GlStateManager.cullFace(1028);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0f, -1.0f, 1.0f);
            GlStateManager.translate(0.2f, 0.35f, 0.5f);
            GlStateManager.rotate(42.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5f, -0.5f, 0.5f);
            if (hasTextureMushroom) {
                this.modelRendererMushroom.render(0.0625f);
            } else {
                blockrendererdispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0f);
            }
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.1f, 0.0f, -0.6f);
            GlStateManager.rotate(42.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, -0.5f, 0.5f);
            if (hasTextureMushroom) {
                this.modelRendererMushroom.render(0.0625f);
            } else {
                blockrendererdispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0f);
            }
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            ((ModelQuadruped)this.mooshroomRenderer.getMainModel()).head.postRender(0.0625f);
            GlStateManager.scale(1.0f, -1.0f, 1.0f);
            GlStateManager.translate(0.0f, 0.7f, -0.2f);
            GlStateManager.rotate(12.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, -0.5f, 0.5f);
            if (hasTextureMushroom) {
                this.modelRendererMushroom.render(0.0625f);
            } else {
                blockrendererdispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0f);
            }
            GlStateManager.popMatrix();
            GlStateManager.cullFace(1029);
            GlStateManager.disableCull();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}

