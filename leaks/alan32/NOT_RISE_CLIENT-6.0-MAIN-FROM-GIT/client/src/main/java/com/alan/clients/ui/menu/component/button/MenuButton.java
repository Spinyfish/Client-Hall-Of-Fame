package com.alan.clients.ui.menu.component.button;

import com.alan.clients.ui.menu.component.MenuComponent;
import com.alan.clients.util.MouseUtil;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;

public class MenuButton extends MenuComponent {

    private final Runnable runnable;

    private final Animation animation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_SINE, 250);

    public MenuButton(double x, double y, double width, double height, Runnable runnable) {
        super(x, y, width, height);
        this.runnable = runnable;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        this.hoverAnimation.run(MouseUtil.isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) ? 80 : 45);
    }

    public void runAction() {
        this.runnable.run();
    }

    public Animation getAnimation() {
        return animation;
    }

    public Animation getHoverAnimation() {
        return hoverAnimation;
    }
}
