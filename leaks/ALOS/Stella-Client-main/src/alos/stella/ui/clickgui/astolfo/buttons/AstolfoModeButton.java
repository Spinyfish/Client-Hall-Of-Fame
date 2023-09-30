package alos.stella.ui.clickgui.astolfo.buttons;

import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.render.DrawUtils;
import alos.stella.value.ListValue;
import org.lwjgl.input.Mouse;

import java.awt.*;

public abstract class AstolfoModeButton extends AstolfoButton {
    public ListValue setting;
    public Color color;
    boolean previousmouse = true;
    public int valueStart = 0;

    public AstolfoModeButton(float x, float y, float width, float height, ListValue set, Color col) {
        super(x, y, width, height);
        setting = set;
        color = col;
    }

    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    @Override
    public void drawPanel(int mouseX, int mouseY) {
        DrawUtils.drawRect(x, y, x + width, y + height, 0xff181A17);
        float f = y + 20.0F;
        Fonts.fontSFUI35.drawStringWithShadow(setting.getName(), (int) (x + 4.0F), (int) (f - (float) this.valueStart - 22), (new Color(255, 255, 255)).getRGB());
        Fonts.fontSFUI35.drawStringWithShadow((String) setting.getValue(), (x + width) - Fonts.fontSFUI35.getStringWidth(setting.getValue()) - 3, f - (float) this.valueStart - 22.0F, -1);
        if (this.isStringHovered(x - 60.0F, f - (float) this.valueStart - 1.0F, x + 39.0F, f - (float) this.valueStart + 6.0F, mouseX - 60, mouseY + 25)) {
            if (Mouse.isButtonDown(0) && !this.previousmouse) {
                String current = (String) setting.getValue();

                setting.set(setting.getValues()[setting.getModeListNumber(current) + 1 >= setting.getValues().length ? 0 : setting.getModeListNumber(current) + 1]);
                this.previousmouse = true;
            }

            if (!Mouse.isButtonDown(0)) {
                this.previousmouse = false;
            }
        }
    }
}
