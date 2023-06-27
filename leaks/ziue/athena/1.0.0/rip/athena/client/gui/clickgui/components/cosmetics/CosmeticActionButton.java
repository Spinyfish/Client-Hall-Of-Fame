package rip.athena.client.gui.clickgui.components.cosmetics;

import java.awt.*;
import rip.athena.client.gui.framework.draw.*;

public class CosmeticActionButton extends CosmeticGenericButton
{
    private String id;
    
    public CosmeticActionButton(final String text, final String id, final int x, final int y, final int width, final int height, final boolean filledBackground) {
        super(text, x, y, width, height, false);
        this.id = id;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(44, 44, 48, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(28, 28, 31, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(54, 54, 59, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(63, 63, 66, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(58, 58, 61, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(76, 76, 79, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        if (this.filledBackground) {
            DrawImpl.drawRect(x, y, width, height, backgroundColor);
        }
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, height - 1, 1, lineColor);
        this.drawShadowUp(x, y, width + 1);
        this.drawShadowLeft(x, y, height + 1);
        this.drawShadowDown(x, y + height + 1, width + 1);
        this.drawShadowRight(x + width + 1, y, height + 1);
        this.drawText(this.text, x + 10, y + (height / 2 - this.getStringHeight(this.text) / 2), textColor);
        this.mouseDown = false;
    }
    
    public String getId() {
        return this.id;
    }
}
