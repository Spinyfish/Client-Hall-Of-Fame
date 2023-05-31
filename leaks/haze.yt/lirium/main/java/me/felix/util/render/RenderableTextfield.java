package me.felix.util.render;

import de.lirium.Client;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import me.felix.util.KeyInputUtil;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderableTextfield {
    public final KeyInputUtil keyInputUtil = new KeyInputUtil();

    private final String text;

    public int x, y, width, height;

    private boolean hovering;

    public RenderableTextfield(String text, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    public void mouseClicked() {
        if (!hovering) return;
        this.keyInputUtil.typing = !keyInputUtil.typing;

    }

    public void drawTextBox(int mouseX, int mouseY) {
        this.hovering = isHovered(mouseX, mouseY, x, y, width, height);


        final AtomicInteger additionalXAxis = new AtomicInteger(0);

        final FontRenderer fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 20);

        if (!keyInputUtil.typedString.isEmpty()) {
            if (fontRenderer.getStringWidth(keyInputUtil.typedString) >= width - 10) {
                final char[] by = keyInputUtil.typedString.toCharArray();
                for (int xChar = 0; xChar < keyInputUtil.typedString.length(); xChar++)
                    additionalXAxis.addAndGet(fontRenderer.getStringWidth(String.valueOf(by[xChar])));
            }
        }

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(40, 40, 40));
        RenderUtil.drawRoundedRectOutline(x, y, width, height, 2, 1, new Color(80, 80, 80));

        StencilUtil.init();
        RenderUtil.drawRoundedRect(x + 2, y, width - 4, height, 3, new Color(40, 40, 40));
        StencilUtil.readBuffer(1);
        if (keyInputUtil.typedString.isEmpty())
            fontRenderer.drawString(text, calculateMiddle(text, fontRenderer, x, width), y + 1, -1);
        else {
            fontRenderer.drawString(keyInputUtil.typedString, x + 3 - ((additionalXAxis.get() - 30) * .2f), y + 1, -1);
        }
        StencilUtil.uninit();
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!hovering) return;
        this.keyInputUtil.registerInput(typedChar, keyCode);
    }


    public int calculateMiddle(String text, FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }

    public boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
