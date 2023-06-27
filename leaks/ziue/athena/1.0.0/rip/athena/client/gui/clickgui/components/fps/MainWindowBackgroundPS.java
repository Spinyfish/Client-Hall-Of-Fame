package rip.athena.client.gui.clickgui.components.fps;

import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.utils.render.*;

public class MainWindowBackgroundPS extends MenuComponent
{
    public MainWindowBackgroundPS(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.setPriority(MenuPriority.LOW);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int width = this.width;
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        DrawUtils.drawRoundedRect(x, y, x + width, y + this.height, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width - 1, y + this.height - 1, 4.0f, new Color(35, 35, 35, 255).getRGB());
    }
}
