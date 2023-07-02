package me.eldodebug.soar.gui.clickgui.comp;

import me.eldodebug.soar.gui.clickgui.category.impl.FeatureCategory;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.settings.Setting;

public class Comp {

    public double x, y, width, height;
    public FeatureCategory parent;
    public Mod mod;
    public Setting setting;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    public void mouseReleased(int mouseX, int mouseY, int state) {}

    public void drawScreen(int mouseX, int mouseY) {}

    public void keyTyped(char typedChar, int keyCode) {}

}
