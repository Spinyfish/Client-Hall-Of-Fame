package rip.athena.client.gui.clickgui.components.cosmetics;

import rip.athena.client.gui.clickgui.components.macros.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;

public class CosmeticGenericButton extends MacroButton
{
    protected boolean filledBackground;
    
    public CosmeticGenericButton(final String text, final int x, final int y, final int width, final int height, final boolean filledBackground) {
        super(text, x, y, width, height, true);
        this.filledBackground = filledBackground;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(30, 30, 30, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(40, 40, 40, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(25, 25, 25, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(30, 30, 30, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(40, 40, 40, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(25, 25, 25, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(150, 150, 150, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(175, 175, 175, 255));
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
        if (this.filledBackground) {}
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width - 1, y + height - 1, 4.0f, lineColor);
        this.drawText(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + (height / 2 - this.getStringHeight(this.text) / 2), textColor);
        this.mouseDown = false;
    }
}
