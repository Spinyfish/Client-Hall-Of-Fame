package wtf.automn.gui.clickgui.phantom.buttons.valuecomps;

import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import wtf.automn.gui.Position;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.gui.clickgui.neverlose.parts.changers.GuiChanger;
import wtf.automn.module.impl.visual.ModuleBlur;
import wtf.automn.module.settings.Setting;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.text.DecimalFormat;

public class Slider extends Component {

    public Slider(float x, float y, float width, float height, Setting setting) {
        super(x, y, width, height + 10, setting);
    }

    private SettingNumber getSetting() {
        return (SettingNumber) setting;
    }

    @Override
    public void drawButton(int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mouseX, mouseY, partialTicks);
        float max = (float) (double) getSetting().max;
        float min = (float) (double) getSetting().min;
        float current = (float) (double) getSetting().value;

//        float width = x + this.width;

        Gui.drawRect(x, y, x + width, y + height, (new Color(12, 12, 12, 140)).getRGB());
        ModuleBlur.drawBlurred(() -> Gui.drawRect(x, y, x + width, y + height, (new Color(12, 12, 12, 220)).getRGB()), false);
        fr.drawString(setting.display + ": " + String.format("%.1f", getSetting().value), x + 4, y + 4, Color.white.getRGB());

        /*
        Changer part
         */
        {
            Position clickPos = new Position(x + 5, y + height - 10, width - 10, 10);
            Gui.drawRect(clickPos.x, clickPos.y + clickPos.height / 2f - 1f, clickPos.x + clickPos.width, clickPos.y + clickPos.height / 2f + 1f, NeverlooseScreen.LIST_SELECTED_COLOR);

            float range = max - min;
            float currentInRange = current - min;
            float perc = currentInRange / range;
            float position = 0;

            if (clickPos.isHovered(mouseX, mouseY) && Mouse.isButtonDown(0)) {
                position = mouseX - clickPos.x;

                float unscramble = position / clickPos.width;
                float unscrambledInRange = range * unscramble;
                float unscrambledOrigin = unscrambledInRange + min;
                getSetting().value = (double) unscrambledOrigin;
            } else {
                position = clickPos.width * perc;
            }
            RenderUtils.circle(clickPos.x + position, clickPos.y + clickPos.height / 2f, 7, NeverlooseScreen.LIST_TEXTURE_COLOR);
        }
    }
}
