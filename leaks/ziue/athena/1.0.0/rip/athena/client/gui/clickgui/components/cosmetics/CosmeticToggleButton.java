package rip.athena.client.gui.clickgui.components.cosmetics;

import rip.athena.client.gui.clickgui.components.macros.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.gui.framework.draw.*;

public class CosmeticToggleButton extends MacroButton
{
    public CosmeticToggleButton(final String text, final int x, final int y, final int width, final int height, final boolean approve) {
        super(text, x, y, width, height, approve);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        DrawImpl.drawRect(x, y, width, height, backgroundColor);
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, height - 1, 1, lineColor);
        this.drawShadowUp(x, y, width + 1);
        this.drawShadowLeft(x, y, height + 1);
        this.drawShadowDown(x, y + height + 1, width + 1);
        this.drawShadowRight(x + width + 1, y, height + 1);
        this.drawText(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + (height / 2 - this.getStringHeight(this.text) / 2), textColor);
        this.mouseDown = false;
    }
}
