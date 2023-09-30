package alos.stella.ui.clickgui.astolfo.buttons;

import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.render.DrawUtils;
import alos.stella.value.IntegerValue;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class AstolfoNumberButton extends AstolfoButton {
    public IntegerValue setting;
    public Color color;

    public boolean dragged;
    public int textWidthInt = 0;

    public AstolfoNumberButton(float x, float y, float width, float height, IntegerValue set, Color col) {
        super(x, y, width, height);

        color = col;
        setting = set;
    }

    @Override
    public void drawPanel(int mouseX, int mouseY) {
        double diff = setting.getMaximum() - setting.getMinimum();

        double percentWidth = (1.0F * (setting.getValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()));

        if (setting.get() == 0){
                textWidthInt = 85;
            }else if(setting.get() <= 9){
            textWidthInt = 85;
        }else if(setting.get() <= 99){
            textWidthInt = 80;
        } else if (setting.get() >= 100){
                textWidthInt = 75;
            }

        if (dragged) {
            double val = setting.getMinimum() + (MathHelper.clamp_double((double) (mouseX - x) / width, 0, 1)) * diff;
            setting.setValue((int) (Math.round(val * 100D)/ 100D));
        }

        DrawUtils.drawRect(x, y, x + width, y + height, 0xff181A17);
        DrawUtils.drawRect(x + 1, y - 2, x + width * percentWidth - 1, y + height - 3, color.getRGB());
        Fonts.fontSFUI35.drawStringWithShadow(setting.getName(), x + 4, y + height / 2 - 5.5f, 0xffffffff);
        Fonts.fontSFUI35.drawStringWithShadow(Math.round(setting.get() * 100D)/ 100D + "", x + textWidthInt, y + height / 2 - 5.5f, 0xffffffff);
    }

    @Override
    public void mouseAction(int mouseX, int mouseY, boolean click, int button) {
        if (isHovered(x + 1, y - 2, x + width - 1, y + height - 3,mouseX, mouseY)) {
            dragged = true;
        }

        if(!click) dragged = false;
    }
}
