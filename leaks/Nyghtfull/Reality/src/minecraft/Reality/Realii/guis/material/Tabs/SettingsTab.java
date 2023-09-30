package Reality.Realii.guis.material.Tabs;

import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.event.value.Value;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.guis.material.Main;
import Reality.Realii.guis.material.Tab;
import Reality.Realii.guis.material.button.Button;
import Reality.Realii.guis.material.button.values.BMode;
import Reality.Realii.guis.material.button.values.BNumbers;
import Reality.Realii.guis.material.button.values.BOption;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.modules.ClientSettings;

import java.util.ArrayList;

public class SettingsTab extends Tab {
    private ArrayList<Button> btns = new ArrayList<>();

    public SettingsTab() {
        name = "Settings";
        for (Value v : ModuleManager.getModuleByClass(ClientSettings.class).getValues()) {
            if (v instanceof Option) {
                Button value = new BOption(startX, startY, v, this);
                btns.add(value);

            } else if (v instanceof Numbers) {
                Button value = new BNumbers(startX, startY, v, this);
                btns.add(value);

            } else if (v instanceof Mode) {
                Button value = new BMode(startX, startY, v, this);
                btns.add(value);
            }
        }
    }

    float startX = Main.windowX + 20;
    float startY = Main.windowY + 70;

    public void render(float mouseX, float mouseY) {
        startX = Main.windowX + 20 + Main.animListX;
        startY = Main.windowY + 70;
        for (Button v : btns) {
            v.x = startX;
            v.y = startY;
            v.draw(mouseX, mouseY);
            if (startX + 100 + FontLoaders.arial18.getStringWidth(v.v.getName()) < Main.windowX + Main.windowWidth) {
                if (v instanceof BOption) {
                    startX += 40 + FontLoaders.arial18.getStringWidth(v.v.getName());
                } else {
                    startX += 80;
                }
            } else {
                startX = Main.windowX + 20 + Main.animListX;
                startY += 30;
            }
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
        startX = Main.windowX + 20 + Main.animListX;
        startY = Main.windowY + 70;
        for (Button v : btns) {
            v.mouseClicked(mouseX, mouseY);
        }
    }
}
