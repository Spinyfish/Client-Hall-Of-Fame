package me.kansio.client.modules.impl.visuals.hud.info;

import me.kansio.client.Client;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.impl.visuals.HUD;
import me.kansio.client.modules.impl.visuals.hud.InfoMode;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.utils.math.BPSUtil;
import me.kansio.client.utils.network.UserUtil;
import me.kansio.client.utils.render.ColorPalette;
import me.kansio.client.utils.render.ColorUtils;
import net.minecraft.util.EnumChatFormatting;

import java.text.DecimalFormat;

public class Sleek extends InfoMode {
    public Sleek() {
        super("Sleek");
    }

    @Override
    public void onRenderOverlay(RenderOverlayEvent event) {
        HUD hud = getHud();
        double bps = BPSUtil.getBPS();
        String userinfo = "§7" + UserUtil.getBuildType(Integer.parseInt(Client.getInstance().getUid())) + " - §f" + Client.getInstance().getUid();

        if (hud.font.getValue()) {
            Fonts.SFRegular.drawStringWithShadow(userinfo, event.getSr().getScaledWidth() - Fonts.SFRegular.getStringWidth(userinfo) - 2, event.getSr().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10), -1);
            Fonts.SFRegular.drawStringWithShadow("BPS: " + EnumChatFormatting.GRAY + new DecimalFormat("0.##").format(bps), 3, event.getSr().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10), ColorUtils.getColorFromHud(5).getRGB());
        } else {
            mc.fontRendererObj.drawStringWithShadow(userinfo, event.getSr().getScaledWidth() - mc.fontRendererObj.getStringWidth(userinfo) - 2, event.getSr().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10), -1);
            mc.fontRendererObj.drawStringWithShadow("BPS: " + EnumChatFormatting.GRAY + new DecimalFormat("0.##").format(bps), 3, event.getSr().getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10), ColorUtils.getColorFromHud(5).getRGB());
        }
    }
}
