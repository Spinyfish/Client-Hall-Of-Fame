package me.kansio.client.modules.impl.visuals.hud.arraylist;

import me.kansio.client.Client;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.visuals.hud.ArrayListMode;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.utils.render.ColorUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Comparator;

public class Intent extends ArrayListMode {

    public Intent() {
        super("Intent");
    }

    @Override
    public void onRenderOverlay(RenderOverlayEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        Client.getInstance().getModuleManager().getModules().sort(Comparator.comparingInt(m ->
                        mc.fontRendererObj.getStringWidth(((Module) m).getName()))
                .reversed()
        );

        int count = 0;

        for (Module m : Client.getInstance().getModuleManager().getModules()) {
            if (!m.isToggled()) {
                continue;
            }

            double offset = count * (mc.fontRendererObj.FONT_HEIGHT + 6);

            if (getHud().font.getValue()) {
                Gui.drawRect(sr.getScaledWidth() - Fonts.SFRegular.getStringWidth(m.getName()) - 10, offset, sr.getScaledWidth() - Fonts.SFRegular.getStringWidth(m.getName()) - 8, 6 + mc.fontRendererObj.FONT_HEIGHT + offset, ColorUtils.getColorFromHud(count).getRGB());
                Gui.drawRect(sr.getScaledWidth() - Fonts.SFRegular.getStringWidth(m.getName()) - 8, offset, sr.getScaledWidth(), 6 + mc.fontRendererObj.FONT_HEIGHT + offset, 0x90000000);
                Fonts.SFRegular.drawStringWithShadow(m.getName(), sr.getScaledWidth() - Fonts.SFRegular.getStringWidth(m.getName()) - 4, (float) (4 + offset), ColorUtils.getColorFromHud(count).getRGB());
            } else {
                Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName()) - 10, offset, sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName()) - 8, 6 + mc.fontRendererObj.FONT_HEIGHT + offset, ColorUtils.getColorFromHud(count).getRGB());
                Gui.drawRect(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName()) - 8, offset, sr.getScaledWidth(), 6 + mc.fontRendererObj.FONT_HEIGHT + offset, 0x90000000);
                mc.fontRendererObj.drawStringWithShadow(m.getName(), sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName()) - 4, (float) (4 + offset), ColorUtils.getColorFromHud(count).getRGB());
            }

            count++;
        }
    }
}
