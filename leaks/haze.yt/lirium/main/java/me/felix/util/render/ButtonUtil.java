package me.felix.util.render;

import de.lirium.Client;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;

import java.awt.*;

public class ButtonUtil {

    public int x, y, width, height;

    public boolean hovering, enabled;

    private int roundnessAnimation = 0;

    public String text;

    public ButtonUtil(String text, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    public void mouseClicked() {
        if (!hovering) return;

        this.enabled = !enabled;

    }

    private static FontRenderer fontRenderer = null;

    public void drawButton(int mouseX, int mouseY) {
        this.hovering = isHovered(mouseX, mouseY, x, y, width, height);

        this.roundnessAnimation = (int) RenderUtil.getAnimationState(roundnessAnimation, !hovering ? 3 : 5, 30);

        RenderUtil.drawRoundedRect(x, y, width, height, roundnessAnimation, new Color(40, 40, 40));
        RenderUtil.drawRoundedRectOutline(x, y, width, height, roundnessAnimation, 1, new Color(80, 80, 80));
        if (fontRenderer == null)
            fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 20);
        fontRenderer.drawString(text, calculateMiddle(text, fontRenderer, x, width), y + 1, -1);

    }

    public int calculateMiddle(String text, FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }

    public boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
