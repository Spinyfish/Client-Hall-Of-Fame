package com.alan.clients.ui.menu.component.button.impl;

import com.alan.clients.ui.menu.component.button.MenuButton;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;

import java.awt.*;

public class MenuTextButton extends MenuButton {

    private static final Font FONT_RENDERER = FontManager.getProductSansBold(24);

    public String name;

    public MenuTextButton(double x, double y, double width, double height, Runnable runnable, String name) {
        super(x, y, width, height, runnable);
        this.name = name;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        // Runs the animation update - keep this
        super.draw(mouseX, mouseY, partialTicks);

        // Colors for rendering
        final double value = getY();
        final double progress = value / this.getY();
        final Color bloomColor = ColorUtil.withAlpha(Color.BLACK, (int) (progress * 150));
        final Color fontColor = ColorUtil.withAlpha(TEXT_PRIMARY, (int) (progress * (150 + this.getHoverAnimation().getValue())));

        // Renders the background of the button
        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5, Color.WHITE));
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, bloomColor));

        // Renders the button text
        UI_BLOOM_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5,
                    ColorUtil.withAlpha(BUTTON, (int) this.getHoverAnimation().getValue() - 15));
//            RenderUtil.roundedOutlineRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, 0.5f, ColorUtil.withAlpha(Color.WHITE, (int) ((int) this.getHoverAnimation().getValue() / 1.7f)));
            RenderUtil.roundedOutlineGradientRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5,
                    1, ColorUtil.withAlpha(BORDER_TWO, 32), ColorUtil.withAlpha(BORDER_ONE, 32));
            FONT_RENDERER.drawCenteredString(this.name, (float) (this.getX() + this.getWidth() / 2.0F),
                    (float) (value + this.getHeight() / 2.0F - 4), fontColor.getRGB());
        });
    }
}
