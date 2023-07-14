/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItemWitch
implements LayerRenderer<EntityWitch> {
    private final RenderWitch witchRenderer;

    public LayerHeldItemWitch(RenderWitch witchRendererIn) {
        this.witchRenderer = witchRendererIn;
    }

    @Override
    public void doRenderLayer(EntityWitch entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ItemStack itemstack = entitylivingbaseIn.getHeldItem();
        if (itemstack != null) {
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            if (this.witchRenderer.getMainModel().isChild) {
                GlStateManager.translate(0.0f, 0.625f, 0.0f);
                GlStateManager.rotate(-20.0f, -1.0f, 0.0f, 0.0f);
                float f = 0.5f;
                GlStateManager.scale(f, f, f);
            }
            ((ModelWitch)this.witchRenderer.getMainModel()).villagerNose.postRender(0.0625f);
            GlStateManager.translate(-0.0625f, 0.53125f, 0.21875f);
            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();
            if (item instanceof ItemBlock && minecraft.getBlockRendererDispatcher().isRenderTypeChest(Block.getBlockFromItem(item), itemstack.getMetadata())) {
                GlStateManager.translate(0.0f, 0.0625f, -0.25f);
                GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(-5.0f, 0.0f, 1.0f, 0.0f);
                float f4 = 0.375f;
                GlStateManager.scale(f4, -f4, f4);
            } else if (item == Items.bow) {
                GlStateManager.translate(0.0f, 0.125f, -0.125f);
                GlStateManager.rotate(-45.0f, 0.0f, 1.0f, 0.0f);
                float f1 = 0.625f;
                GlStateManager.scale(f1, -f1, f1);
                GlStateManager.rotate(-100.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(-20.0f, 0.0f, 1.0f, 0.0f);
            } else if (item.isFull3D()) {
                if (item.shouldRotateAroundWhenRendering()) {
                    GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.translate(0.0f, -0.0625f, 0.0f);
                }
                this.witchRenderer.transformHeldFull3DItemLayer();
                GlStateManager.translate(0.0625f, -0.125f, 0.0f);
                float f2 = 0.625f;
                GlStateManager.scale(f2, -f2, f2);
                GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(0.0f, 0.0f, 1.0f, 0.0f);
            } else {
                GlStateManager.translate(0.1875f, 0.1875f, 0.0f);
                float f3 = 0.875f;
                GlStateManager.scale(f3, f3, f3);
                GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(-60.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(-30.0f, 0.0f, 0.0f, 1.0f);
            }
            GlStateManager.rotate(-15.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(40.0f, 0.0f, 0.0f, 1.0f);
            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

