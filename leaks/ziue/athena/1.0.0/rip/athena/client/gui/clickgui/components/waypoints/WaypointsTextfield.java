package rip.athena.client.gui.clickgui.components.waypoints;

import rip.athena.client.gui.clickgui.components.mods.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.gui.clickgui.*;
import org.lwjgl.input.*;
import rip.athena.client.utils.render.*;
import net.minecraft.client.*;

public class WaypointsTextfield extends SearchTextfield
{
    protected int placeholderText;
    protected int textPadding;
    
    public WaypointsTextfield(final TextPattern pattern, final int x, final int y, final int width, final int height, final int placeholderText) {
        super(pattern, x, y, width, height);
        this.textPadding = 5;
        this.placeholderText = placeholderText;
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(54, 54, 57, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(50, 50, 52, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(59, 59, 62, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(120, 120, 120, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(29, 29, 32, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(25, 25, 28, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(36, 36, 40, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + this.minOffset * 2;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        if (this.tab && !Keyboard.isKeyDown(15)) {
            this.tab = false;
        }
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        int textColor = this.getColor(DrawType.TEXT, this.lastState);
        final int textColorDisabled = this.getColor(DrawType.TEXT, ButtonState.DISABLED);
        DrawUtils.drawRoundedRect(x - 4, y - 4, x + width + 5, y + height + 5, 0.0f, 83886080);
        DrawUtils.drawRoundedRect(x - 3, y - 3, x + width + 4, y + height + 4, 0.0f, 369098752);
        DrawUtils.drawRoundedRect(x - 2, y - 2, x + width + 3, y + height + 3, 0.0f, 587202560);
        DrawUtils.drawRoundedRect(x - 1, y - 1, x + width + 2, y + height + 2, 0.0f, lineColor);
        DrawUtils.drawRoundedRect(x, y, x + width + 1, y + height + 1, 0.0f, lineColor);
        DrawUtils.drawRoundedRect(x, y, x + width + 1, y + height + 1, 0.0f, backgroundColor);
        String textToDraw = this.text;
        if (this.isPasswordField()) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < textToDraw.length(); ++i) {
                builder.append("*");
            }
            textToDraw = builder.toString();
        }
        boolean drawPointer = false;
        if (this.focused && (System.currentTimeMillis() - this.lineTime) % this.lineRefreshTime * 2L >= this.lineRefreshTime) {
            drawPointer = true;
        }
        int labelWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw + 1);
        int comp = 0;
        int toRender = this.index;
        while (labelWidth >= width + this.textPadding * 2) {
            if (comp < this.index) {
                textToDraw = textToDraw.substring(1);
                labelWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw + 1);
                --toRender;
            }
            else if (comp > this.index) {
                textToDraw = textToDraw.substring(0, textToDraw.length() - 1);
                labelWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw + 1);
            }
            ++comp;
        }
        if (drawPointer) {
            if (toRender > textToDraw.length()) {
                toRender = textToDraw.length() - 1;
            }
            if (toRender < 0) {
                toRender = 0;
            }
            final int textHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
            this.drawVerticalLine(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw.substring(0, toRender)) + 1, y + height / 2 - textHeight / 2, textHeight, 1, textColor);
        }
        final int renderIndex = comp;
        final int renderStopIndex = comp + textToDraw.length();
        while (this.index > this.text.length()) {
            --this.index;
        }
        int xAdd = this.textPadding;
        if (textToDraw.isEmpty() && !this.isFocused()) {
            textToDraw = "" + this.placeholderText;
            xAdd = width / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("" + this.placeholderText) / 2;
            textColor = WaypointsTextfield.PLACEHOLDER_COLOR.getRGB();
            if (textToDraw.length() == 3) {
                textColor = textColorDisabled;
            }
        }
        Minecraft.getMinecraft().fontRendererObj.drawString(textToDraw, x + xAdd, y + height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, textColor);
        if (this.lastState == ButtonState.HOVER && this.mouseDown) {
            this.focused = true;
            this.lineTime = this.getLinePrediction();
            int position = x + this.textPadding;
            if (mouseX < position) {
                this.index = 0;
                return;
            }
            float bestDiff = 1000.0f;
            int bestIndex = -1;
            for (int j = renderIndex; j < renderStopIndex; ++j) {
                if (this.text.length() > j) {
                    final int diff = Math.abs(mouseX - position);
                    if (bestDiff > diff) {
                        bestDiff = (float)diff;
                        bestIndex = j;
                    }
                    position += Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.text.charAt(j) + "");
                }
            }
            if (mouseX > position) {
                this.index = this.text.length();
            }
            else if (bestIndex != -1) {
                this.index = bestIndex;
            }
            else {
                this.index = 0;
            }
        }
        this.mouseDown = false;
    }
}
