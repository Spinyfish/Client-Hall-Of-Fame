package de.lirium.impl.module.impl.ui;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import de.lirium.base.drag.DragHandler;
import de.lirium.base.drag.Draggable;
import de.lirium.impl.events.HotbarRenderEvent;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.render.ColorUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import me.felix.shader.access.ShaderAccess;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

@ModuleFeature.Info(name = "Better Hotbar", category = ModuleFeature.Category.UI, description = "Renders a deep balls hotbar")
public class BetterHotbarFeature extends ModuleFeature {

    private final Animation animation = new Animation();

    private FontRenderer fontRenderer;

    public final Draggable hotbarPosition = DragHandler.setupDrag(this, "Hotbar", 300, 400, false);

    @EventHandler
    public final Listener<HotbarRenderEvent> renderEventListener = e -> {
        e.renderDefaultHotbar = false;
        if (fontRenderer == null)
            fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 10);
    };

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = this::renderHotbar;

    private final Color color = new Color(141, 73, 218, 144);

    private void renderHotbar(Render2DEvent event) {

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();


        hotbarPosition.setObjectHeight(22);
        hotbarPosition.setObjectWidth(91 * 2);

        final int i = (int) hotbarPosition.getXPosition();

        float x = mc.player.inventory.currentItem * 20;

        ShaderAccess.blurShaderRunnables.add(() -> RenderUtil.drawRoundedRect(hotbarPosition.getXPosition(), hotbarPosition.getYPosition(), 91 * 2, 22, 3, Color.GRAY));
        //   JHLabsShaderRenderer.renderShadowOnObject((int) hotbarPosition.getXPosition(), (int) hotbarPosition.getYPosition(), (int) 91 * 2, (int) 22, 12, Color.black, () -> RenderUtil.drawRoundedRect(hotbarPosition.getXPosition(), hotbarPosition.getYPosition(), 91 * 2, 22, 3, Color.GRAY));


        animation.update();
        animation.animate(x, .01, Easings.BOUNCE_IN);

        RenderUtil.drawRoundedRect(i + animation.getValue(), hotbarPosition.getYPosition(), 22, 22, 3, color);
        ShaderAccess.bloomRunnables.add(() -> RenderUtil.drawRoundedRect(hotbarPosition.getXPosition(), hotbarPosition.getYPosition(), 91 * 2, 22, 3, ColorUtil.getColor(2000, 20)));

        for (int j = 0; j < 9; ++j) {
            final int k = (int) hotbarPosition.getXPosition() + j * 20 + 2;
            final int l = (int) hotbarPosition.getYPosition() + 7;
            int finalJ = j;
            renderHotBarItem(j, k, l - 4, event.getPartialTicks(), mc.player);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("selectedItemName");

        if (mc.ingameGUI.remainingHighlightTicks > 0 && !(mc.ingameGUI.highlightingItemStack.func_190926_b())) {
            String s = (mc.ingameGUI.highlightingItemStack.getDisplayName());

            if (mc.ingameGUI.highlightingItemStack.hasDisplayName()) {
                s = TextFormatting.ITALIC + s;
            }

            int i = (int) ((hotbarPosition.getXPosition() - fontRenderer.getStringWidth(s)) / 2);
            int j = (int) hotbarPosition.getYPosition();

            if (!this.mc.playerController.shouldDrawHUD()) {
                j += 14;
            }

            int k = (int) ((float) mc.ingameGUI.remainingHighlightTicks * 256.0F / 10.0F);

            if (k > 255) {
                k = 255;
            }

            if (k > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                fontRenderer.drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.mcProfiler.endSection();
    }

    private void renderHotBarItem(final int index, final int xPos, final int yPos, final float partialTicks, final EntityPlayer entityPlayer) {
        final ItemStack itemstack = entityPlayer.inventory.mainInventory.get(index);
        final RenderItem itemRenderer = mc.getRenderItem();

        if (itemstack != null) {
            final float f = (float) itemstack.animationsToGo - partialTicks;

            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                final float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float) (xPos + 8), (float) (yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float) (-(xPos + 8)), (float) (-(yPos + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }

            itemRenderer.renderItemOverlays(mc.fontRendererObj, itemstack, xPos, yPos);
        }
    }

}
