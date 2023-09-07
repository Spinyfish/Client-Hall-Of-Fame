package kevin.persional.milk.guis.clickgui.buttons;

import kevin.persional.milk.guis.font.FontLoaders;
import kevin.persional.milk.utils.key.ClickUtils;
import kevin.module.BooleanValue;
import kevin.utils.RenderUtils;

import java.awt.*;

public class BooleanButton extends Button {
    public BooleanValue value;
    public BooleanButton(BooleanValue value){
        this.value = value;
    }

    @Override
    public void drawButton(int x, int y, int mx, int my, float pticks, float alpha) {
        int intalpha = (int)(alpha * 255);
        FontLoaders.novo20.drawString(value.getName() + ":", x, y, new Color(255, 255, 255, intalpha).getRGB());
        Color color =value.get() ? new Color(0, 255, 0, intalpha).brighter() : new Color(255, 0, 0, intalpha).brighter();
        RenderUtils.drawSector(x + 303, y + 2, -2, 360, 4, color);
        super.drawButton(x, y, mx, my, pticks, alpha);
    }

    @Override
    public void clickButton(int x, int y, int mx, int my) {
        if(ClickUtils.isClickable(x + 303 - 2, y, x + 303 + 2, y + 4, mx, my)){
            value.set(!value.get());
        }
        super.clickButton(x, y, mx, my);
    }
}
