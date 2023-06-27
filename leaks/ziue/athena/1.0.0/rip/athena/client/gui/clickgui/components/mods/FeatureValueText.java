package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.draw.*;
import java.awt.*;

public class FeatureValueText extends FeatureText
{
    public FeatureValueText(final String text, final String tooltip, final int x, final int y) {
        super(text, tooltip, x, y);
    }
    
    public FeatureValueText(final String text, final int x, final int y) {
        super(text, x, y);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(158, 158, 158, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(178, 178, 178, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(200, 200, 200, 255));
    }
}
