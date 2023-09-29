package com.alan.clients.ui.menu.component.button.impl;

import com.alan.clients.util.MouseUtil;

public class MenuFeedBackTextButton extends MenuTextButton {

    public String feedback;
    public String originalName;


    public MenuFeedBackTextButton(double x, double y, double width, double height, Runnable runnable, String name, String feedback) {
        super(x, y, width, height, runnable, name);
        this.feedback = feedback;
        this.originalName = name;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);

        if (!MouseUtil.isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY)) {
            this.name = originalName;
        }
    }

    @Override
    public void runAction() {
        super.runAction();
        this.name = feedback;
    }
}
