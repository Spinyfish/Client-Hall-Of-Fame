package com.alan.clients.module.impl.render;

import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.DragValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import util.time.StopWatch;

import java.awt.*;

@ModuleInfo(name = "module.render.hotbar.name", description = "module.render.hotbar.description", category = Category.RENDER, autoEnabled = true, allowDisable = false)
public class HotBar extends Module {

    private DragValue structure = new DragValue("", this, new Vector2d(200, 200), false, true);
    private float rPosX;
    private final StopWatch stopwatch = new StopWatch();
    private ScaledResolution scaledResolution;

    @EventLink()
    public final Listener<Render2DEvent> onPreMotionEvent = event -> {
        scaledResolution = event.getScaledResolution();

        NORMAL_BLUR_RUNNABLES.add(() -> renderHotBar(scaledResolution, Color.BLACK));

        NORMAL_RENDER_RUNNABLES.add(this::renderHotBarScroll);

        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            renderHotBar(scaledResolution, getTheme().getDropShadow());
        });
    };

    @EventLink()
    public final Listener<LimitedRender2DEvent> onLimitedRender2D = event -> {
        if (mc.getRenderViewEntity() instanceof EntityPlayer) {

            scaledResolution = event.getScaledResolution();
            final EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();

            LIMITED_PRE_RENDER_RUNNABLES.add(() -> {
                renderHotBar(scaledResolution, getTheme().getBackgroundShade());

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
            });
        }
    };

    private void renderHotBar(final ScaledResolution scaledResolution, final Color color) {
        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            structure.position = new Vector2d((int) (scaledResolution.getScaledWidth() / 2.0F - 91), (int) (scaledResolution.getScaledHeight() - 22 - 4));
            structure.scale = new Vector2d(91 * 2, 22);

            RenderUtil.roundedRectangle(structure.position.x, structure.position.y, structure.scale.x, structure.scale.y, 7, color);
        }
    }

    private void renderHotBarScroll() {
        if (scaledResolution == null || !(mc.getRenderViewEntity() instanceof EntityPlayer)) return;

        final EntityPlayer entityplayer = (EntityPlayer) mc.thePlayer;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        final int i = scaledResolution.getScaledWidth() / 2;

        final Interface interfaceModule = Client.INSTANCE.getModuleManager().get(Interface.class);

        if (interfaceModule == null || interfaceModule.smoothHotBar.getValue()) {
            for (int time = 0; time < stopwatch.getElapsedTime(); ++time) {
                rPosX = MathUtil.lerp(rPosX, i - 91 - 1 + entityplayer.inventory.currentItem * 20, 0.055f);
            }

            stopwatch.reset();
        } else {
            rPosX = i - 91 - 1 + entityplayer.inventory.currentItem * 20;
        }

        RenderUtil.roundedRectangle(rPosX + 1, scaledResolution.getScaledHeight() - 22 - 4, 22,
                22, 7, getTheme().getBackgroundShade());
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
