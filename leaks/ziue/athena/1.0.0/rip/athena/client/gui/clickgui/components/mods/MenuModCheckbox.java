package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;

public class MenuModCheckbox extends MenuCheckbox
{
    public MenuModCheckbox(final int x, final int y, final int width, final int height) {
        super("", x, y, width, height);
        this.textOffset = 0;
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(0, 0, 0, 0));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(100, 100, 100, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(70, 70, 70, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(150, 150, 150, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(43, 43, 43, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(53, 53, 53, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(48, 48, 48, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(59, 59, 59, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        if (backgroundColor == this.getColor(DrawType.BACKGROUND, ButtonState.ACTIVE)) {
            RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + this.width), (float)(y + this.height), 4.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB());
            RoundedUtils.drawRoundedRect((float)(x + 3), (float)(y + 3), (float)(x + this.width - 3), (float)(y + this.height - 3), 4.0f, Color.BLACK.getRGB());
        }
        else if (backgroundColor == this.getColor(DrawType.BACKGROUND, ButtonState.HOVER)) {
            RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + this.width), (float)(y + this.height), 4.0f, Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB());
        }
        else if (backgroundColor == this.getColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE)) {
            RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + this.width), (float)(y + this.height), 4.0f, Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB());
            RoundedUtils.drawRoundedRect((float)(x + 3), (float)(y + 3), (float)(x + this.width - 3), (float)(y + this.height - 3), 4.0f, Color.BLACK.getRGB());
        }
        else {
            RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + this.width), (float)(y + this.height), 4.0f, Athena.INSTANCE.getThemeManager().getTheme().getThirdColor().getRGB());
        }
        this.drawTooltip();
        this.mouseDown = false;
    }
}
