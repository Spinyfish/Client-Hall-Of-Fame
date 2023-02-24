package me.kansio.client.modules.impl.visuals.hud.watermark;

import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.impl.visuals.hud.WaterMarkMode;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.utils.render.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;

public class Intent extends WaterMarkMode {

    public Intent() {
        super("Intent");
    }

    @Override
    public void onRenderOverlay(RenderOverlayEvent event) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(4, 4, 0);
        GlStateManager.scale(2, 2, 1);
        GlStateManager.translate(-4, -4, 0);
        if (getHud().font.getValue()) {
            Fonts.SFRegular.drawString(ChatUtil.translateColorCodes(getHud().clientName.getValue()), 4, 4, ColorUtils.getColorFromHud(1).getRGB());
        } else {
            mc.fontRendererObj.drawString(ChatUtil.translateColorCodes(getHud().clientName.getValue()), 4, 4, ColorUtils.getColorFromHud(1).getRGB());
        }
        GlStateManager.popMatrix();
    }
}
