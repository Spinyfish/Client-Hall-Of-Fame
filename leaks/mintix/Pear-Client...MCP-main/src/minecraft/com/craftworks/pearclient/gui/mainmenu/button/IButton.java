package com.craftworks.pearclient.gui.mainmenu.button;

import com.craftworks.pearclient.util.GLRectUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.math.MathHelper;

import java.awt.*;

public class IButton {

    private final String icon;
    private int x, y;
    private final int w, h;

    public IButton(String icon, int x, int y) {
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.w = 20;
        this.h = 20;
    }

    public void renderButton(int x, int y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        DrawUtil.drawRoundedRectangle(x, y, w - 3, h - 3, 4, new Color(0, 0, 0, 100).getRGB(), 0);
        GLRectUtils.drawRoundedOutline(x, y, x + w - 3, y + h - 3, 4.0f, 1.0f, isHovered(mouseX, mouseY) ? new Color(60, 232, 118, 200).getRGB() : new Color(60, 232, 118, 100).getRGB());

        DrawUtil.drawPicture(x + 4, y + 4, w - 11, h - 11, Color.WHITE.getRGB(), icon);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return MathHelper.withinBox(x - 3, y - 3, w, h, mouseX, mouseY);
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

    public String getIcon() {
        return icon;
    }
}
