package me.felix.tabgui.handlers;

import de.lirium.Client;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;

import java.awt.*;

public class TabGUICategory {
    private final ModuleFeature.Category category;

    public TabGUICategory(final ModuleFeature.Category category) {
        this.category = category;
    }

    private FontRenderer fontRenderer;

    public void doRender(final int x, final int y) {

        if (fontRenderer == null)
            fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 19);

        if (Client.INSTANCE.tabGui.selectedCategory == this.category)
            RenderUtil.drawRoundedRect(x - 1, y - 1, Client.INSTANCE.tabGui.width - 4, 15, 4, new Color(45, 45, 45));

        fontRenderer.drawString(category.getDisplay(), calculateMiddle(category.getDisplay(), fontRenderer, x, Client.INSTANCE.tabGui.width - 4), y, -1);

    }

    public int calculateMiddle(String text, FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }
}
