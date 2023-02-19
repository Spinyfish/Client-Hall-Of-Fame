package alos.stella.ui.clickgui.astolfo.buttons;

import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.render.DrawUtils;
import alos.stella.value.BoolValue;

import java.awt.*;

public class AstolfoBooleanButton extends AstolfoButton {
    public BoolValue setting;
    public Color color;

    public AstolfoBooleanButton(float x, float y, float width, float height, BoolValue set, Color col) {
        super(x, y, width, height);
        setting = set;
        color = col;
    }

    @Override
    public void drawPanel(int mouseX, int mouseY) {
        DrawUtils.drawRect(x, y, x + width, y + height, 0xff181A17);
        if(setting.get()) DrawUtils.drawRect(x + 1, y + 5, x + width - 1, y + height - 12, color.getRGB());
        Fonts.fontSFUI35.drawStringWithShadow(setting.getName(), x + 4, y + height/2 - 6.5f, 0xffffffff);
    }

    @Override
    public void mouseAction(int mouseX, int mouseY, boolean click, int button) {
        if(isHovered(x, y - 3, x + width, y + height - 3, mouseX, mouseY) && click) {
            setting.set(!setting.get());
        }
    }
}
