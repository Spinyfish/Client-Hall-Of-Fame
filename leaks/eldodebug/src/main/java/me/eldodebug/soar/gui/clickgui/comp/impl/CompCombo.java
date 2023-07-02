package me.eldodebug.soar.gui.clickgui.comp.impl;

import me.eldodebug.soar.gui.clickgui.category.impl.FeatureCategory;
import me.eldodebug.soar.gui.clickgui.comp.Comp;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.settings.Setting;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;

public class CompCombo extends Comp {

    public CompCombo(double x, double y, FeatureCategory parent, Mod mod, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.mod = mod;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        
        RoundedUtils.drawRound((float) (parent.getX() + x - 70), (float) (parent.getY() + y), (float) FontUtils.regular20.getStringWidth(setting.getName() + ": " + setting.getValString()) + 5, 11, 3, ColorUtils.getBackgroundColor(2));
        FontUtils.regular20.drawString(setting.getName() + ": " + setting.getValString(), (int)(parent.getX() + x - 68), (int)(parent.getY() + y + 2), ColorUtils.getFontColor(2).getRGB());
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (MouseUtils.isInside(mouseX, mouseY, parent.getX() + x - 70, parent.getY() + y, 70, 10) && mouseButton == 0) {
            int max = setting.getOptions().size();
            if (parent.modeIndex + 1 >= max) {
                parent.modeIndex = 0;
            } else {
                parent.modeIndex++;
            }
            setting.setValString(setting.getOptions().get(parent.modeIndex));
        }
    }
}
