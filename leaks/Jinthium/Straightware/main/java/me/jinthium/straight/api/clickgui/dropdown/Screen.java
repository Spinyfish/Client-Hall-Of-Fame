package me.jinthium.straight.api.clickgui.dropdown;

import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;

public interface Screen extends Util, MinecraftInstance {

    default void onDrag(int mouseX, int mouseY) {

    }

    void initGui();

    void keyTyped(char typedChar, int keyCode);

    void drawScreen(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int button);

    void mouseReleased(int mouseX, int mouseY, int state);

}