package wtf.automn.gui.clickgui.neverlose.parts.changers;

import org.lwjgl.input.Mouse;
import wtf.automn.gui.Position;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.text.DecimalFormat;

public class GuiChangerNumber extends GuiChanger<SettingNumber> {

    public GuiChangerNumber(SettingNumber s, float x, float y, int width, int height) {
        super(s, x, y, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        float max = (float)(double)setting.max;
        float min = (float)(double)setting.min;
        float current = (float)(double)setting.value;

        float width = pos.x+pos.width;
        String valDisplay = new DecimalFormat("#.##").format(current);
        float valueWidth = settingFont.getStringWidth(valDisplay);

        float fontY = pos.y+(pos.height/2-(settingFont.getFontHeight()/1.5F));

        RenderUtils.setGLColor(INACTIVE_COLOR);
        settingFont.drawString(setting.display, pos.x+3, fontY, INACTIVE_COLOR);


        settingFont.drawString(valDisplay, width-valueWidth -15, fontY - 10, ACTIVE_COLOR);

        /*
        Changer part
         */
        {
            Position clickPos = new Position(width-125, pos.y, 100, 20);
            drawRect(clickPos.x, clickPos.y+pos.height/2-0.5F, clickPos.x+clickPos.width, clickPos.y+ clickPos.height/2+0.5F, NeverlooseScreen.LIST_SELECTED_COLOR);

            float range = max-min;
            float currentInRange = current-min;
            float perc = currentInRange/range;
            float position = 0;

            if(clickPos.isHovered(mouseX, mouseY) && Mouse.isButtonDown(0)) {
                position = mouseX-clickPos.x;

                float unscramble = position/clickPos.width;
                float unscambledInRange = range*unscramble;
                float unscrambledOrigin = unscambledInRange+min;
                setting.value = (double) unscrambledOrigin;
            }else {
                position = clickPos.width*perc;
            }
            RenderUtils.circle(clickPos.x+position, clickPos.y+clickPos.height/2+1, 7, NeverlooseScreen.LIST_TEXTURE_COLOR);
        }

    }
}
