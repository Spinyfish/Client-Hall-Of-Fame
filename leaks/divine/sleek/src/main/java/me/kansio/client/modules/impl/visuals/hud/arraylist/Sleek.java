package me.kansio.client.modules.impl.visuals.hud.arraylist;

import me.kansio.client.Client;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.visuals.HUD;
import me.kansio.client.modules.impl.visuals.hud.ArrayListMode;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.utils.render.ColorUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;

public class Sleek extends ArrayListMode {

    public Sleek() {
        super("Sleek");
    }

    @Override
    public void onRenderOverlay(RenderOverlayEvent event) {
        HUD hud = getHud();
        HUD.notifications = hud.noti.getValue() && hud.isToggled();
        int y = hud.arrayListY.getValue().intValue();
        int index = 0;
        Color color;


        if (hud.font.getValue()) {
            ArrayList<Module> sorted = (ArrayList<Module>) Client.getInstance().getModuleManager().getModulesSorted(Fonts.SFRegular);
            sorted.removeIf(m -> !m.isToggled());

            if (hud.hideRender.getValue()) sorted.removeIf(m -> m.getCategory() == ModuleCategory.VISUALS);
            for (Module mod : sorted) {
                if (!mod.isToggled()) continue;

                index++;

                color = ColorUtils.getColorFromHud(y);

                Module lastModule = sorted.get(index - 1);


                String name = mod.getName() + "§7" + mod.getFormattedSuffix();
                float xPos = event.getSr().getScaledWidth() - Fonts.SFRegular.getStringWidth(name) - 6;

                Gui.drawRect(xPos - 1.5, y - 2, event.getSr().getScaledWidth(), Fonts.SFRegular.getHeight() + y + 1, new Color(0, 0, 0, hud.bgalpha.getValue()).getRGB());
                switch (getHud().line.getValue()) {
                    case "None":
                        break;
                    case "Wrapped":
                        Gui.drawRect(xPos - 2.5, y - 1, xPos - 1.5, Fonts.SFRegular.getHeight() + y + 1, color.getRGB());
                        break;
                }
                if (sorted.size() > index) {
                    Module nextMod = sorted.get(index);

                    String nextName = nextMod.getName() + "§7" + nextMod.getFormattedSuffix();
                    float nextxPos = (float) (event.getSr().getScaledWidth() - Fonts.SFRegular.getStringWidth(nextName) - 7.5);

                    switch (getHud().line.getValue()) {
                        case "None":
                            break;
                        case "Wrapped":
                            Gui.drawRect(xPos - 2.5, Fonts.SFRegular.getHeight() + y + 1, nextxPos, Fonts.SFRegular.getHeight() + y + 2, color.getRGB());
                            break;
                    }
                } else {
                    switch (getHud().line.getValue()) {
                        case "None":
                            break;
                        case "Wrapped":
                            Gui.drawRect(xPos - 2.5, Fonts.SFRegular.getHeight() + y + 1, xPos + 100, Fonts.SFRegular.getHeight() + y + 2, color.getRGB());
                            break;
                    }
                }

                Fonts.SFRegular.drawStringWithShadow(name, (float) (xPos + 1.5), (float) (0.5 + y), color.getRGB());
                y = y + 10;


            }
        } else {
            ArrayList<Module> sorted = (ArrayList<Module>) Client.getInstance().getModuleManager().getModulesSorted(mc.fontRendererObj);
            sorted.removeIf(m -> !m.isToggled());

            if (hud.hideRender.getValue()) sorted.removeIf(m -> m.getCategory() == ModuleCategory.VISUALS);
            for (Module mod : sorted) {
                if (!mod.isToggled()) continue;

                index++;

                Module lastModule = sorted.get(index - 1);

                color = ColorUtils.getColorFromHud(y);

                String name = mod.getName() + "§7" + mod.getFormattedSuffix();
                float xPos = event.getSr().getScaledWidth() - mc.fontRendererObj.getStringWidth(name) - 6;

                Gui.drawRect(xPos - 1.5, y - 1, event.getSr().getScaledWidth(), mc.fontRendererObj.FONT_HEIGHT + y + 1, new Color(0, 0, 0, hud.bgalpha.getValue()).getRGB());
                switch (getHud().line.getValue()) {
                    case "None":
                        break;
                    case "Wrapped":
                        Gui.drawRect(xPos - 2.5, y - 1, xPos - 1.5, mc.fontRendererObj.FONT_HEIGHT + y + 1, color.getRGB());
                        break;
                }
                if (sorted.size() > index) {
                    Module nextMod = sorted.get(index);

                    String nextName = nextMod.getName() + "§7" + nextMod.getFormattedSuffix();
                    float nextxPos = (float) (event.getSr().getScaledWidth() - mc.fontRendererObj.getStringWidth(nextName) - 7.5);

                    switch (getHud().line.getValue()) {
                        case "None":
                            break;
                        case "Wrapped":
                            Gui.drawRect(xPos - 2.5, mc.fontRendererObj.FONT_HEIGHT + y + 1, nextxPos, mc.fontRendererObj.FONT_HEIGHT + y + 2, color.getRGB());
                            break;
                    }
                } else {
                    switch (getHud().line.getValue()) {
                        case "None":
                            break;
                        case "Wrapped":
                            Gui.drawRect(xPos - 2.5, mc.fontRendererObj.FONT_HEIGHT + y + 1, xPos + 100, mc.fontRendererObj.FONT_HEIGHT + y + 2, color.getRGB());
                            break;
                    }
                }

                mc.fontRendererObj.drawStringWithShadow(name, (float) (xPos + 1.5), (float) (0.5 + y), color.getRGB());
                y = y + 11;


            }
        }
    }
}
