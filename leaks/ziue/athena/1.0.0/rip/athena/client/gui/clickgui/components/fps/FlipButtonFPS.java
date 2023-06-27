package rip.athena.client.gui.clickgui.components.fps;

import rip.athena.client.gui.clickgui.components.macros.*;
import rip.athena.client.gui.clickgui.pages.fps.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.utils.render.*;

public class FlipButtonFPS extends FlipButton
{
    private BlacklistModule module;
    
    public FlipButtonFPS(final BlacklistModule module, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.module = module;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int linePopupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        DrawUtils.drawRoundedRect(x - 1, y - 1, x + width + 1, y + height + 1, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, new Color(35, 35, 35, 255).getRGB());
        int color = this.active ? FlipButtonFPS.NORMAL_ON : FlipButtonFPS.NORMAL_OFF;
        if (this.lastState == ButtonState.HOVER || this.lastState == ButtonState.HOVERACTIVE) {
            color = (this.active ? FlipButtonFPS.HOVER_ON : FlipButtonFPS.HOVER_OFF);
        }
        if (this.active) {
            DrawUtils.drawRoundedRect(x, y, x + width / 2, y + height, 4.0f, color);
        }
        else {
            DrawUtils.drawRoundedRect(x + width - width / 2, y, x + width, y + height, 4.0f, color);
        }
        this.mouseDown = false;
    }
    
    public BlacklistModule getModule() {
        return this.module;
    }
}
