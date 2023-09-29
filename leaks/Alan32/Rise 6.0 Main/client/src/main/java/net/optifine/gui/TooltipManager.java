package net.optifine.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TooltipManager {
    private final GuiScreen guiScreen;
    private final TooltipProvider tooltipProvider;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public TooltipManager(final GuiScreen guiScreen, final TooltipProvider tooltipProvider) {
        this.guiScreen = guiScreen;
        this.tooltipProvider = tooltipProvider;
    }

    public void drawTooltips(final int x, final int y, final List buttonList) {
        if (Math.abs(x - this.lastMouseX) <= 5 && Math.abs(y - this.lastMouseY) <= 5) {
            final int i = 700;

            if (System.currentTimeMillis() >= this.mouseStillTime + (long) i) {
                final GuiButton guibutton = GuiScreenOF.getSelectedButton(x, y, buttonList);

                if (guibutton != null) {
                    final Rectangle rectangle = this.tooltipProvider.getTooltipBounds(this.guiScreen, x, y);
                    String[] astring = this.tooltipProvider.getTooltipLines(guibutton, rectangle.width);

                    if (astring != null) {
                        if (astring.length > 8) {
                            astring = Arrays.copyOf(astring, 8);
                            astring[astring.length - 1] = astring[astring.length - 1] + " ...";
                        }

                        if (this.tooltipProvider.isRenderBorder()) {
                            final int j = -528449408;
                            this.drawRectBorder(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, j);
                        }

                        Gui.drawRect(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, -536870912);

                        for (int l = 0; l < astring.length; ++l) {
                            final String s = astring[l];
                            int k = 14540253;

                            if (s.endsWith("!")) {
                                k = 16719904;
                            }

                            final FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
                            fontrenderer.drawStringWithShadow(s, (float) (rectangle.x + 5), (float) (rectangle.y + 6 + l * 11), k);
                        }
                    }
                }
            }
        } else {
            this.lastMouseX = x;
            this.lastMouseY = y;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private void drawRectBorder(final int x1, final int y1, final int x2, final int y2, final int col) {
        Gui.drawRect(x1, y1 - 1, x2, y1, col);
        Gui.drawRect(x1, y2, x2, y2 + 1, col);
        Gui.drawRect(x1 - 1, y1, x1, y2, col);
        Gui.drawRect(x2, y1, x2 + 1, y2, col);
    }
}
