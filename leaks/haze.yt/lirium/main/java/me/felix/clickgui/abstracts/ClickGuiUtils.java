package me.felix.clickgui.abstracts;

import de.lirium.util.render.FontRenderer;

public class ClickGuiUtils {

    public int calculateMiddle(String text, FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }

    public int calculateMousePosition(int mouse, int position) {
        return mouse + position;
    }

}
