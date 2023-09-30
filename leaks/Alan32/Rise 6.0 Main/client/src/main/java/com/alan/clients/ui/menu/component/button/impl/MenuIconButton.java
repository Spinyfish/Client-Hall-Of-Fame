package com.alan.clients.ui.menu.component.button.impl;

import com.alan.clients.ui.menu.component.button.MenuButton;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class MenuIconButton extends MenuButton {

    private final ResourceLocation resourceLocation;

    public MenuIconButton(double x, double y, double width, double height, Runnable runnable, ResourceLocation resourceLocation) {
        super(x, y, width, height, runnable);
        this.resourceLocation = resourceLocation;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        // Runs the animation update - keep this
        super.draw(mouseX, mouseY, partialTicks);

        // Colors for rendering
        final double value = this.getY();
        final double progress = value / this.getY();
        final Color bloomColor = ColorUtil.withAlpha(Color.BLACK, (int) (progress * 100));
        final Color fontColor = ColorUtil.withAlpha(Color.WHITE, (int) (progress * (50 + this.getHoverAnimation().getValue() * 2)));

        // Renders the background of the button
        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5, Color.WHITE));
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, bloomColor));

        // Renders the button icon TODO: Might change to font (depends on alan)
        UI_BLOOM_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, ColorUtil.withAlpha(Color.WHITE, (int) this.getHoverAnimation().getValue() / 3));
            RenderUtil.roundedOutlineRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, 0.5f, ColorUtil.withAlpha(Color.WHITE, (int) this.getHoverAnimation().getValue() / 3));
            RenderUtil.image(resourceLocation, this.getX() + this.getWidth() / 2.0F - 8, value + this.getHeight() / 2.0F - 8, 16, 16, fontColor);
        });
    }
}
