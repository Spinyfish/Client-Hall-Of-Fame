package com.alan.clients.ui.click.standard.components.category;

import com.alan.clients.Client;
import com.alan.clients.module.api.Category;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.impl.rise.FontRenderer;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;

import java.awt.*;

public final class CategoryComponent implements InstanceAccess {

    private final Font icon = FontManager.getIconsOne(32);

    public final Category category;
    private long lastTime = 0;
    private double selectorOpacity;

    private float x, y;

    public CategoryComponent(final Category category) {
        this.category = category;
    }

    public void render(final double offset, final double sidebarWidth, final double opacity, final Screen selectedScreen) {
        final RiseClickGUI clickGUI = Client.INSTANCE.getStandardClickGUI();

        if (System.currentTimeMillis() - lastTime > 300) lastTime = System.currentTimeMillis();
        final long time = System.currentTimeMillis();

        /* Gets position depending on sidebar animation */
        x = (float) (clickGUI.position.x - (69 - sidebarWidth) - 21);
        y = (float) (clickGUI.position.y + offset) + 16;

        /* Animations */
        if (selectedScreen.equals(category.getClickGUIScreen())) {
            selectorOpacity = Math.min((selectorOpacity + (time - lastTime) * 2), 255);
        } else {
            selectorOpacity = Math.max((selectorOpacity - (time - lastTime) * 2), 0);
        }

        final double spacer = 4;
        final double width = nunitoSmall.width(Localization.get(category.getName())) + spacer + FontManager.getIconsOne(17).width(category.getIcon());

        /* Draws selection */
        RenderUtil.roundedRectangle(x + 0.5, y - 5.5, width + 12, 15, 4,
                ColorUtil.withAlpha(getTheme().getAccentColor(new Vector2d(0, y / 5D)), (int) (Math.min(selectorOpacity, opacity) * 255)).darker());

        /* Draws category icon */
//        this.icon.drawString(category.getIcon(), (float) (x + selectorOpacity / 80f - 8), y - 2, color);

        /* Draws category name */
//        if (selectedScreen.equals(category.getClickGUIScreen())) {
//        clickGUI.nunitoSmall.drawString(category.getName(), (float) (x + selectorOpacity / 80f + 5), y + 1, new Color(0, 0, 0, Math.min(100, (int) opacity)).hashCode());
//        }

        int color = new Color(255, 255, 255, Math.min(selectedScreen.equals(category.getClickGUIScreen()) ? 255 : 200, (int) opacity)).hashCode();

        category.getFontRenderer().drawString(category.getIcon(), (float) (x + selectorOpacity / 80f + 3), y, color);

        clickGUI.nunitoSmall.drawString(Localization.get(category.getName()), (float) (x + selectorOpacity / 80f + 3 + spacer) +
                FontManager.getIconsOne(17).width(category.getIcon()), y, color);

        lastTime = time;
    }

    public void click(final float mouseX, final float mouseY, final int button) {
        final boolean left = button == 0;
        if (GUIUtil.mouseOver(x - 11, y - 5, 70, 22, mouseX, mouseY) && left) {
            this.getStandardClickGUI().switchScreen(this.category);
        }
    }

    public void bloom(final double opacity) {
        final double width = nunitoSmall.width(Localization.get(category.getName())) + 12;
        RenderUtil.roundedRectangle(x + 0.5, y - 5.5, width + 12, 15, 5,
                ColorUtil.withAlpha(getTheme().getAccentColor(new Vector2d(0, y / 5D)), (int) Math.min(selectorOpacity, Math.max(0, opacity - 100))));
    }
}