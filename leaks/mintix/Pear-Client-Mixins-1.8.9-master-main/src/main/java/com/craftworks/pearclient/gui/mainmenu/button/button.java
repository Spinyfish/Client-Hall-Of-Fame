package com.craftworks.pearclient.gui.mainmenu.button;

import com.craftworks.pearclient.animation.Animate;
import com.craftworks.pearclient.animation.Easing;
import com.craftworks.pearclient.util.GLRectUtils;
import com.craftworks.pearclient.util.font.FontUtil;
import com.craftworks.pearclient.util.math.MathHelper;

import java.awt.*;

public class button {
    private final Animate animate = new Animate();

    private final String text;
    private int x, y;
    private final int w, h;

    public button(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.w = 150;
        this.h = 20;
        animate.setEase(Easing.LINEAR).setMin(0).setMax(25).setSpeed(200);
    }

    public void renderButton(int x, int y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        animate.update().setReversed(!isHovered(mouseX, mouseY));

        GLRectUtils.drawRoundedRect(x, y, x + w - 77, y + h,4.0f, new Color(0, 0, 0, animate.getValueI() + 100).getRGB());

        GLRectUtils.drawRoundedOutline(x, y, x + w - 77, y + h, 4.0f, 2.0f, new Color(60, 232, 118, animate.getValueI() + 100).getRGB());

        FontUtil.regular.drawString(
                text,
                x + w / 2f - FontUtil.regular.getStringWidth(text) / 2f - 40,
                y + h / 2f - 3,
                -1
        );
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return MathHelper.withinBox(x, y, w - 80, h, mouseX, mouseY);
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return text;
    }
}
