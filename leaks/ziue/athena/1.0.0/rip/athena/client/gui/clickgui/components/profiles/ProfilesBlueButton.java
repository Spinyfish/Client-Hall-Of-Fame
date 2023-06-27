package rip.athena.client.gui.clickgui.components.profiles;

import rip.athena.client.gui.clickgui.components.macros.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;

public class ProfilesBlueButton extends MacroButton
{
    public ProfilesBlueButton(final String text, final int x, final int y, final int width, final int height) {
        super(text, x, y, width, height, true);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(25, 25, 25, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(30, 30, 30, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(25, 25, 25, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
}
