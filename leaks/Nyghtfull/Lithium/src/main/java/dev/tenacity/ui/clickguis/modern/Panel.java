package dev.tenacity.ui.clickguis.modern;

import dev.tenacity.utils.Utils;

public abstract class Panel implements Utils {

    public float x, y, bigRecty;

    abstract public void initGui();

    public abstract void keyTyped(char typedChar, int keyCode);

    abstract public void drawScreen(int mouseX, int mouseY);

    abstract public void mouseClicked(int mouseX, int mouseY, int button);

    abstract public void mouseReleased(int mouseX, int mouseY, int state);

}
