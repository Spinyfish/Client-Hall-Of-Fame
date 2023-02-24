package me.kansio.client.modules.impl.visuals.hud.watermark;

import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.impl.visuals.hud.WaterMarkMode;
import me.kansio.client.utils.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;
import oshi.hardware.Processor;

import java.awt.*;

public class Vital extends WaterMarkMode {

    public Vital() {
        super("Vital");
    }

    @Override
    public void onRenderOverlay(RenderOverlayEvent event) {
        int color = (new Color(87, 124, 255)).getRGB();
        Processor[] aprocessor = (new SystemInfo()).getHardware().getProcessors();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.5D, 2.5D, 2.5D);
        if (getHud().font.getValue()) {
            Fonts.SFRegular.drawStringWithShadow("S", 1.0F, 0.F, color);
        } else {
            mc.fontRendererObj.drawStringWithShadow("S", 1.0F, 0.F, color);
        }
        GlStateManager.popMatrix();
        if (getHud().font.getValue()) {
            Fonts.SFRegular.drawStringWithShadow("leek", 18.0F, 12.0F, color);
            Fonts.SFRegular.drawStringWithShadow("fps: " + Minecraft.getDebugFPS(), 1.0F, 22.0F, color);
            Fonts.SFRegular.drawStringWithShadow("gpu: " + GL11.glGetString(7937), 1.0F, 32.0F, color);
            Fonts.SFRegular.drawStringWithShadow("cpu: " + aprocessor[0].toString(), 1.0F, 42.0F, color);
        } else {
            mc.fontRendererObj.drawStringWithShadow("leek", 18.0F, 12.0F, color);
            mc.fontRendererObj.drawStringWithShadow("fps: " + Minecraft.getDebugFPS(), 1.0F, 22.0F, color);
            mc.fontRendererObj.drawStringWithShadow("gpu: " + GL11.glGetString(7937), 1.0F, 32.0F, color);
            mc.fontRendererObj.drawStringWithShadow("cpu: " + aprocessor[0].toString(), 1.0F, 42.0F, color);
        }
    }
}
