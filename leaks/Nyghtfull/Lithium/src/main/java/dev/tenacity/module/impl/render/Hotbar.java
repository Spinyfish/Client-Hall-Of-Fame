package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.event.impl.render.elements.RenderHotbarEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import javafx.scene.transform.Scale;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;

import static net.minecraft.client.gui.Gui.drawRect;
import static net.minecraft.client.gui.GuiIngame.widgetsTexPath;

public class Hotbar extends Module {

    public Hotbar() {
        super("Hotbar", Category.RENDER, "Renders a custom hotbar.");
    }

    @Override
    public void onRenderHotbarEvent(RenderHotbarEvent event) {
        event.cancel();
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            Gui.drawRect(
                    sr.getScaledWidth() / 2.0F - 91,
                    sr.getScaledHeight() - 22,
                    sr.getScaledWidth() / 2.0F + 91,
                    sr.getScaledHeight(),
                    0x77000000
            );

            Gui.drawRect(
                    sr.getScaledWidth() / 2.0F - 91 + mc.thePlayer.inventory.currentItem * 20,
                    sr.getScaledHeight() - 22,
                    sr.getScaledWidth() / 2.0F - 91 + mc.thePlayer.inventory.currentItem * 20 + 22,
                    sr.getScaledHeight(),
                    0x50FFFFFF
            );

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int index = 0; index < 9; ++index) {
                int x = sr.getScaledWidth() / 2 - 90 + index * 20 + 2;
                int y = sr.getScaledHeight() - 19;

                mc.ingameGUI.renderHotbarItem(index, x, y, mc.timer.renderPartialTicks, mc.thePlayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if (!event.isBloom())
            return;

        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            Gui.drawRect(
                    sr.getScaledWidth() / 2.0F - 91,
                    sr.getScaledHeight() - 22,
                    sr.getScaledWidth() / 2.0F + 91,
                    sr.getScaledHeight(),
                    0x77000000
            );

            Gui.drawRect(
                    sr.getScaledWidth() / 2.0F - 91 + mc.thePlayer.inventory.currentItem * 20,
                    sr.getScaledHeight() - 22,
                    sr.getScaledWidth() / 2.0F - 91 + mc.thePlayer.inventory.currentItem * 20 + 22,
                    sr.getScaledHeight(),
                    0x50FFFFFF
            );

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }
}
