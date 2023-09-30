package de.fanta.clickgui.defaultgui.components;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.setting.Setting;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;

import java.util.ArrayList;

public class DropdownBox {
    public float x;
    public float y;
    public CategoryPanel panel;
    public Setting setting;
    private boolean isOpened;
    private int ani = 0;

    public ArrayList<DropdownButton> dropdownButtons;

    public float buttonY;

    public DropdownBox(Setting setting, CategoryPanel panel, float x, float y) {
        this.setting = setting;
        this.panel = panel;
        this.x = x;
        this.y = y;

        this.dropdownButtons = new ArrayList<>();

        de.fanta.setting.settings.DropdownBox dropdownBox = (de.fanta.setting.settings.DropdownBox) setting.getSetting();

        float buttonY = 9;
        for(String o : dropdownBox.getOptions()) {
            if(!o.equals(dropdownBox.curOption)) {
                dropdownButtons.add(new DropdownButton(o, this, x, y + buttonY));
                buttonY += 9;
            }
        }

        this.buttonY = buttonY - 9;
    }

    public void drawDropdownBox(float mouseX, float mouseY) {

        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff + 5 && mouseY >= y + yOff + 1 && mouseX <= x + xOff + 105 && mouseY <= y + yOff + 9;

        int color = hovering ? Colors.getColor(200, 50, 0, 255) : Colors.getColor(50, 50, 50, 255);

        RenderUtil.rectangle(x + xOff + 5, y + yOff + 1, x + xOff + 105, y + yOff + 9, Colors.getColor(0, 0, 0, 255));
        RenderUtil.rectangle(x + xOff + 6, y + yOff + 2, x + xOff + 104, y + yOff + 8, color);

        Client.INSTANCE.unicodeBasicFontRenderer.drawStringScaled(((de.fanta.setting.settings.DropdownBox) setting.getSetting()).curOption, (int) (x + xOff + 6), (int) (y + yOff), Colors.getColor(200, 200, 200, 255), 0.8);
        Client.INSTANCE.unicodeBasicFontRenderer.drawStringScaled(setting.getName(), (int) (x + xOff + 110), (int) (y + yOff - 2), -1 , 1);

        if(this.isOpened) {
            dropdownButtons.forEach(dropdownButton -> dropdownButton.drawDropdownButton(mouseX, mouseY));
        }
    }

    public void dropdownBoxClicked(float mouseX, float mouseY, int mouseButton) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff + 5 && mouseY >= y + yOff + 1 && mouseX <= x + xOff + 105 && mouseY <= y + yOff + 9;

        if(hovering) this.isOpened = !this.isOpened;

        if(this.isOpened) {
            dropdownButtons.forEach(dropdownButton -> dropdownButton.dropdownButtonClicked(mouseX, mouseY, mouseButton));
        }
    }
}
