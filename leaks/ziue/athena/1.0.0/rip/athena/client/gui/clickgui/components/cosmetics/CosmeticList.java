package rip.athena.client.gui.clickgui.components.cosmetics;

import rip.athena.client.gui.framework.components.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import net.minecraft.client.*;

public class CosmeticList extends MenuDropdown
{
    protected int cursorWidth;
    
    public CosmeticList(final Class<?> theEnum, final int x, final int y, final int width, final int height) {
        super(theEnum, x, y);
        this.cursorWidth = 25;
        this.height = height;
        this.width = width;
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(162, 162, 162, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(182, 182, 182, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(24, 24, 27, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(53, 53, 55, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(120, 120, 120, 255));
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (!this.disabled) {
            final boolean inRange = false;
            if (mouseX >= x && mouseX <= x + width + this.arrowOffset - 1 && mouseY >= y && mouseY <= y + this.height + 1) {
                state = ButtonState.HOVER;
                if (this.mouseDown) {
                    if (mouseX < x + this.cursorWidth - 1) {
                        if (this.index - 1 >= 0) {
                            --this.index;
                            this.onAction();
                        }
                        else {
                            this.index = this.values.length - 1;
                            this.onAction();
                        }
                    }
                    else if (mouseX > x + width - this.cursorWidth - 1) {
                        if (this.index + 1 < this.values.length) {
                            ++this.index;
                            this.onAction();
                        }
                        else {
                            this.index = 0;
                            this.onAction();
                        }
                    }
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (state == ButtonState.HOVER || state == ButtonState.HOVERACTIVE) {
            this.setPriority(MenuPriority.HIGH);
        }
        else {
            this.setPriority(MenuPriority.MEDIUM);
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + 1;
        final int height = this.height;
        final int popupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, height - 1, 1, lineColor);
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        final int defBg = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        final int cache = backgroundColor;
        if (mouseX > x + this.cursorWidth - 1) {
            backgroundColor = defBg;
        }
        DrawImpl.drawRect(x, y, this.cursorWidth, height + 1, popupColor);
        DrawImpl.drawRect(x + 1, y + 1, this.cursorWidth - 2, height - 1, backgroundColor);
        this.drawText("<", x + 3 + this.cursorWidth / 2 - this.getStringWidth("<") / 2, y + height / 2 - this.getStringHeight("<") / 2, textColor);
        if (mouseX < x + width - this.cursorWidth - 1) {
            backgroundColor = defBg;
        }
        else {
            backgroundColor = cache;
        }
        DrawImpl.drawRect(x + width - this.cursorWidth, y, this.cursorWidth, height + 1, popupColor);
        DrawImpl.drawRect(x + width - this.cursorWidth + 1, y + 1, this.cursorWidth - 2, height - 1, backgroundColor);
        this.drawText(">", x + width - this.cursorWidth + 3 + this.cursorWidth / 2 - this.getStringWidth(">") / 2, y + height / 2 - this.getStringHeight(">") / 2, textColor);
        final String text = this.values[this.index].toUpperCase();
        this.drawText(text, x + width / 2 - this.getStringWidth(text) / 2, y + height / 2 - this.getStringHeight(text) / 2, textColor);
        this.mouseDown = false;
    }
    
    @Override
    public void drawText(final String text, final int x, final int y, final int color) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
    }
    
    @Override
    public int getStringWidth(final String string) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
    }
    
    @Override
    public int getStringHeight(final String string) {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
}
