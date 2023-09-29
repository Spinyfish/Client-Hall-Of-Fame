package de.fanta.clickgui.defaultgui.components;

import de.fanta.Client;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;

public class DropdownButton {
    public float x;
    public float y;
    private String option;
    private DropdownBox dropdownBox;

    public DropdownButton(String option, DropdownBox dropdownBox, float x, float y) {
        this.dropdownBox = dropdownBox;
        this.option = option;
        this.x = x;
        this.y = y;
    }

    public void drawDropdownButton(float mouseX, float mouseY) {
        float xOff = dropdownBox.panel.cateButton.panel.dragX;
        float yOff = dropdownBox.panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff + 6 && mouseY >= y + yOff && mouseX <= x + xOff + 104 && mouseY <= y + yOff + 8;

        int color = hovering ? Colors.getColor(200, 50, 0, 255) : Colors.getColor(50, 50, 50, 255);

        RenderUtil.rectangle(x + xOff + 6, y + yOff, x + xOff + 104, y + yOff + 8, color);

        Client.INSTANCE.unicodeBasicFontRenderer.drawStringScaledShadow(option, (int) (x + xOff + 5), (int) (y + yOff) - 1, Colors.getColor(0, 0, 0, 255), 0.8, yOff, yOff);
    }

    public void dropdownButtonClicked(float mouseX, float mouseY, float mouseButton) {
        float xOff = dropdownBox.panel.cateButton.panel.dragX;
        float yOff = dropdownBox.panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff + 5 && mouseY >= y + yOff && mouseX <= x + xOff + 105 && mouseY <= y + yOff + 8;

        if(hovering) {
            String lastCurOption = ((de.fanta.setting.settings.DropdownBox) dropdownBox.setting.getSetting()).curOption;
            ((de.fanta.setting.settings.DropdownBox) dropdownBox.setting.getSetting()).curOption = option;
            this.option = lastCurOption;
        }
    }
}
