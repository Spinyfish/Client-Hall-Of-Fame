package io.github.raze.modules.system.ingame;

import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.modules.system.BaseModule;

public abstract class ModuleIngame extends BaseModule {

    public double x, y, lastX, lastY;
    public double width, height;

    public ModuleIngame(String name, String description, ModuleCategory category, int keyCode) {
        super(name, description, category, keyCode);
    }

    public ModuleIngame(String name, String description, ModuleCategory category) {
        super(name, description, category);
    }

    public abstract void renderIngame();
    public abstract void renderDummy();

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

    public double getLastX() {
        return lastX;
    }

    public void setLastX(double lastX) {
        this.lastX = lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public void setLastY(double lastY) {
        this.lastY = lastY;
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
}
