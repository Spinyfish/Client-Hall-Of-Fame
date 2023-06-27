package rip.athena.client.gui.clickgui.components.fps;

import rip.athena.client.gui.clickgui.components.cosmetics.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;

public class FPSGenericButton extends CosmeticGenericButton
{
    public FPSGenericButton(final String text, final int x, final int y, final int width, final int height, final boolean filledBackground) {
        super(text, x, y, width, height, filledBackground);
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
}
