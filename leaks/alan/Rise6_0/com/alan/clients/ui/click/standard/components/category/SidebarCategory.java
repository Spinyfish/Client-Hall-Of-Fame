package com.alan.clients.ui.click.standard.components.category;

import com.alan.clients.Client;
import com.alan.clients.module.api.Category;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.impl.HomeScreen;
import com.alan.clients.ui.click.standard.screen.impl.SpeedBuilderScreen;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SidebarCategory implements InstanceAccess {

    private final List<CategoryComponent> categories = Arrays.stream(Category.values())
            .map(CategoryComponent::new).collect(Collectors.toList());
    /* Information */
    public double sidebarWidth = 100;
    private double opacity, fadeOpacity;
    private long lastTime = 0;

    public void renderSidebar(final float mouseX, final float mouseY) {
        /* ClickGUI */
        final RiseClickGUI clickGUI = Client.INSTANCE.getStandardClickGUI();

        /* Animations */
        final long time = System.currentTimeMillis();

        if (lastTime == 0) lastTime = time;

        final boolean hoverCategory = clickGUI.selectedScreen instanceof HomeScreen || clickGUI.selectedScreen instanceof SpeedBuilderScreen;

        if (GUIUtil.mouseOver(clickGUI.position.x, clickGUI.position.y, opacity > 0 ? 70 : 10, clickGUI.scale.y, mouseX, mouseY) || !hoverCategory) {
            opacity = Math.min(opacity + (time - lastTime) * 2, 255);
//            sidebarWidth = Math.min((sidebarWidth + (time - lastTime) * 5 * (0.1 - sidebarWidth / 750)), 89);
        } else {
            opacity = Math.max(opacity - (time - lastTime), 0);
//            sidebarWidth = Math.max((sidebarWidth - (time - lastTime) * 5 * (0.1 - sidebarWidth / 750)), 55);
        }

        if (GUIUtil.mouseOver(clickGUI.position.x, clickGUI.position.y, fadeOpacity > 0 ? 70 : 10, clickGUI.scale.y, mouseX, mouseY) && hoverCategory) {
            fadeOpacity = Math.min(fadeOpacity + (time - lastTime) * 2, 255);
        } else {
            fadeOpacity = Math.max(fadeOpacity - (time - lastTime), 0);
        }

        /* Fades out background */
        if (fadeOpacity > 0) {
            RenderUtil.roundedRectangle(clickGUI.position.x, clickGUI.position.y, clickGUI.scale.x, clickGUI.scale.y, getStandardClickGUI().getRound(),
                    new Color(10, 10, 10, (int) (fadeOpacity * 0.25)));
        }

        /* Drop shadow */
//        RenderUtil.horizontalGradient(clickGUI.position.x + sidebarWidth - 3 - 10, clickGUI.position.y, 20, clickGUI.scale.y, new Color(0, 0, 0, hoverCategory ? (int) (fadeOpacity * 0.25) : 100),
//                new Color(0, 0, 0, 0));

        /* Sidebar background */
        lastTime = time;
        final Color color = new Color(clickGUI.sidebarColor.getRed(), clickGUI.sidebarColor.getGreen(), clickGUI.sidebarColor.getBlue(), (int) Math.min(opacity, clickGUI.sidebarColor.getAlpha()));
//        RenderUtil.dropShadow(4, clickGUI.position.x + 20, clickGUI.position.y, (float) sidebarWidth - 20, clickGUI.scale.y, 60, 1);
        RenderUtil.roundedRectangle(clickGUI.position.x, clickGUI.position.y, sidebarWidth, clickGUI.scale.y, getStandardClickGUI().getRound(), color);
        RenderUtil.rectangle(clickGUI.position.x + 15, clickGUI.position.y, sidebarWidth - 15, clickGUI.scale.y, color);

        /* Renders all categories */
        double offsetTop = 10, offsetBottom = -17;

        for (final CategoryComponent category : categories) {
            if (category.category.top) {
                category.render((offsetTop += 19.5), sidebarWidth, (int) opacity, clickGUI.selectedScreen);
            } else {
                category.render(clickGUI.scale.y + (offsetBottom -= 19.5), sidebarWidth, (int) opacity, clickGUI.selectedScreen);
            }
        }

        final float posX = clickGUI.position.getX() + 9;
        final float posY = clickGUI.position.getY() + ((19.5F + 30) / 2.0F - nunitoLarge.height() / 2.0F);

        FontManager.getProductSansRegular(32).drawString(Client.NAME, posX + 5, posY + 2, ColorUtil.withAlpha(Color.WHITE, (int) opacity).hashCode());
        FontManager.getProductSansRegular(16).drawString(Client.VERSION, posX + 5 + FontManager.getProductSansRegular(32).width(Client.NAME), posY, new Color(255, 255, 255, (int) Math.min(opacity, 100)).hashCode());

//        this.poppinsBold.drawString(Rise.NAME, (float) (clickGUI.position.x + sidebarWidth - 56), clickGUI.position.y + 12, new Color(0, 0, 0, (int) Math.min(opacity, 100)).hashCode());
//        this.poppinsBold.drawString(Rise.NAME, (float) (clickGUI.position.x + sidebarWidth - 56), clickGUI.position.y + 11, new Color(clickGUI.accentColor.getRed(), clickGUI.accentColor.getGreen(), clickGUI.accentColor.getBlue(), (int) opacity).hashCode());
    }

    public void bloom() {
        for (final CategoryComponent category : categories) {
            category.bloom(opacity);
        }
    }

    public void clickSidebar(final float mouseX, final float mouseY, final int button) {
        if (opacity > 0) {
            for (final CategoryComponent category : categories) {
                category.click(mouseX, mouseY, button);
            }
        }
    }
}
