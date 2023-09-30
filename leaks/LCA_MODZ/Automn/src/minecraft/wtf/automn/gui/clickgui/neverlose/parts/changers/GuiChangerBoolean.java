package wtf.automn.gui.clickgui.neverlose.parts.changers;

import org.lwjgl.input.Mouse;
import wtf.automn.gui.Position;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;

public class GuiChangerBoolean extends GuiChanger<SettingBoolean> {

    public GuiChangerBoolean(SettingBoolean s, float x, float y, int width, int height) {
        super(s, x, y, width, height);
    }

    private boolean next = true;

    @Override
    public void draw(int mouseX, int mouseY) {
        float fontY = pos.y+(pos.height/2-(settingFont.getFontHeight()/1.5F));
        settingFont.drawString(setting.display, pos.x+3, fontY, setting.value ? ACTIVE_COLOR : INACTIVE_COLOR);

        /*
        Changer
         */
        {
            Position clickPos = new Position(pos.x+pos.width-50, pos.y+6, 25, pos.height-12);
            RenderUtils.drawRoundedRect(clickPos.x, clickPos.y, clickPos.width, clickPos.height, 6, setting.value ? NeverlooseScreen.LIST_SELECTED_COLOR : new Color(30, 30, 30).getRGB());
            RenderUtils.circle(setting.value ? clickPos.x+ clickPos.width-6 : clickPos.x+6, clickPos.y+6, 16, setting.value ? NeverlooseScreen.LIST_TEXTURE_COLOR : new Color(60, 60 ,60).getRGB());
            if(clickPos.isHovered(mouseX, mouseY) && next && Mouse.isButtonDown(0)){
                setting.value = !setting.value;
                next = false;
            }
        }
        if(!Mouse.isButtonDown(0)) next = true;
    }
}
