package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.visual.Shaders;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import club.pulsive.impl.util.render.shaders.Blur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

@ModuleInfo(name = "Hobar", category = Category.CLIENT)
public class RiseHotbar extends Module {

    private float rPosX;
    private final TimerUtil stopwatch = new TimerUtil();
    private ScaledResolution scaledResolution;
    private final int color = new Color(0, 0, 0, 50).hashCode();
    private final int color2 = new Color(0, 0, 0, 50).hashCode();

    @EventHandler
    private final Listener<ShaderEvent> shaderEventListener = event -> {
        if (!(mc.gameSettings.showDebugInfo && mc.playerController.isSpectator())) {
            //renderHotBar(scaledResolution, color2);
//            renderHotBarScroll(color);
        }
    };


    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        scaledResolution = event.getScaledResolution();
        final EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();

        Gui.drawRect(0, 0, 0, 0, 0);
        if (!(mc.gameSettings.showDebugInfo && mc.playerController.isSpectator())) {
            renderHotBar(scaledResolution, color2);
            renderHotBarScroll(color);

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();

                for (int j = 0; j < 9; ++j) {
                    final int k = scaledResolution.getScaledWidth() / 2 - 90 + j * 20 + 2;
                    final int l = scaledResolution.getScaledHeight() - 16 - 3;
                    renderHotBarItem(j, k, l - 4, event.getPartialTicks(), entityplayer);
                }

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();

//            StencilUtil.initStencilToWrite();
//            renderHotBar(scaledResolution, 1);
//            StencilUtil.readStencilBuffer(1);
//            Blur.renderBlur(Shaders.blurRadius.getValue().floatValue());
//            StencilUtil.uninitStencilBuffer();

            //BLOOM_RUNNABLES.add(() -> renderHotBar(scaledResolution, 1));
        }
    };

    private void renderHotBar(final ScaledResolution scaledResolution, final int color) {
        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            RoundedUtil.drawRoundedRect(scaledResolution.getScaledWidth() / 2f - 91, scaledResolution.getScaledHeight() - 22 - 4,
                    (scaledResolution.getScaledWidth() / 2f - 91) + (91 * 2), (scaledResolution.getScaledHeight() - 22 - 4) + 22, 0,
                    1 == color ? Color.BLACK.getRGB() : new Color(0, 0, 0, 150).getRGB());

            StencilUtil.initStencilToWrite();
            RoundedUtil.drawRoundedRect(scaledResolution.getScaledWidth() / 2f - 91, scaledResolution.getScaledHeight() - 22 - 4,
                    (scaledResolution.getScaledWidth() / 2f - 91) + (91 * 2), (scaledResolution.getScaledHeight() - 22 - 4) + 22, 0,
                    -1);
            StencilUtil.readStencilBuffer(1);
            Blur.renderBlur(15);
            StencilUtil.uninitStencilBuffer();


           // Pulsive.INSTANCE.getBlurrer().blur(scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 22 - 4, 91 * 2, 22, true);
            Pulsive.INSTANCE.getBlurrer().bloom(scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 22 - 4, 91 * 2, 22, 15, 150);
        }
    }

    private void renderHotBarScroll(final int color) {
        if (scaledResolution == null) return;

        final EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();

        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            final int i = scaledResolution.getScaledWidth() / 2;

            for (int time = 0; time < stopwatch.elapsed(); ++time) {
                rPosX = MathUtil.lerp(rPosX, i - 91 - 1 + entityplayer.inventory.currentItem * 20, 0.055f);
            }

            stopwatch.reset();

            RoundedUtil.drawRoundedRect(rPosX + 1, scaledResolution.getScaledHeight() - 22 - 4, rPosX + 1 + 22,
                    (scaledResolution.getScaledHeight() - 22 - 4) + 22, 0, new Color(0, 0, 0, 150).getRGB());

            StencilUtil.initStencilToWrite();
            RoundedUtil.drawRoundedRect(rPosX + 1, scaledResolution.getScaledHeight() - 22 - 4, rPosX + 1 + 22,
                    (scaledResolution.getScaledHeight() - 22 - 4) + 22, 0, -1);
            StencilUtil.readStencilBuffer(1);
            Blur.renderBlur(8);
            StencilUtil.uninitStencilBuffer();

            Pulsive.INSTANCE.getBlurrer().bloom(Math.round(rPosX) + 1, scaledResolution.getScaledHeight() - 22 - 4, 22, 22, 15, 90);
        }
    }

    private void renderHotBarItem(final int index, final int xPos, final int yPos, final float partialTicks, final EntityPlayer entityPlayer) {
        final ItemStack itemstack = entityPlayer.inventory.mainInventory[index];
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
