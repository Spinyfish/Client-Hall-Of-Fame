package io.github.raze.screen.collection.menu.components.api;

import io.github.raze.utilities.system.BaseUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class BaseComponent implements BaseUtility {

    public double x, y;
    public double dragX, dragY;
    public double width, height;

    public boolean dragging, open;

    public ScaledResolution scaledResolution = new ScaledResolution(mc);

    public void render(int mouseX, int mouseY, float partialTicks) {
        scaledResolution = new ScaledResolution(mc);
    };

    public void click(int mouseX, int mouseY, int mouseButton) {

    };

    public void release(int mouseX, int mouseY, int button) {

    };

    public void type(char typed, int keyCode) {

    };

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDragX() {
        return dragX;
    }

    public void setDragX(double dragX) {
        this.dragX = dragX;
    }

    public double getDragY() {
        return dragY;
    }

    public void setDragY(double dragY) {
        this.dragY = dragY;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
