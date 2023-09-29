package wtf.automn.gui.clickgui.phantom.buttons.valuecomps;

import com.sun.jdi.BooleanValue;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import wtf.automn.Automn;
import wtf.automn.gui.Position;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.module.impl.visual.ModuleBlur;
import wtf.automn.module.settings.Setting;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;

public class Checkbox extends Component {

    private boolean next = true;

    public Checkbox(float x, float y, float width, float height, Setting setting) {
        super(x, y, width, height, setting);
    }

    @Override
    public void drawButton(int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mouseX, mouseY, partialTicks);
        Gui.drawRect(x, y, x + width, y + height, (new Color(12, 12, 12, 140)).getRGB());
        ModuleBlur.drawBlurred(() -> Gui.drawRect(x, y, x + width, y + height, (new Color(12, 12, 12, 220)).getRGB()), false);
        fr.drawString(setting.display, x + 4, y + 4, Color.white.getRGB());
        Position clickPos = new Position(x + width - 20, y + 4, 18, fr.getFontHeight());
        RenderUtils.drawRoundedRect2(clickPos.x, clickPos.y, clickPos.width, clickPos.height, clickPos.height / 2f, ((SettingBoolean) setting).value ? NeverlooseScreen.LIST_SELECTED_COLOR : new Color(30, 30, 30).getRGB());
        RenderUtils.circle(((SettingBoolean) setting).value ? clickPos.x + clickPos.width - clickPos.height / 2f - 0.5f : clickPos.x + 0.5f + clickPos.height / 2f, clickPos.y + clickPos.height / 2f, clickPos.height - 2, ((SettingBoolean) setting).value ? NeverlooseScreen.LIST_TEXTURE_COLOR : new Color(60, 60, 60).getRGB());
        if (clickPos.isHovered(mouseX, mouseY) && next && Mouse.isButtonDown(0)) {
            setting.value = !((SettingBoolean) setting).value;
            next = false;
        }
        if(!Mouse.isButtonDown(0)) next = true;
    }
}
