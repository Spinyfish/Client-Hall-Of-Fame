package de.fanta.clickgui.defaultgui.components;

import de.fanta.Client;
import de.fanta.clickgui.defaultgui.Panel;
import de.fanta.module.Module;
import de.fanta.utils.Colors;

public class CategoryButton {
    public Panel panel;
    public Module.Type type;
    public boolean isActive;
    public CategoryPanel catePanel;
    private float x;
    private float y;

    public CategoryButton(Module.Type type, Panel panel, float x, float y) {
        this.type = type;
        this.panel = panel;
        this.x = x;
        this.y = y;

        catePanel = new CategoryPanel(this, x + 50, 53);
    }

    public void drawCateButton(float mouseX, float mouseY) {
        boolean hovering = mouseX >= x + panel.dragX && mouseY >= y + panel.dragY && mouseX <= x + panel.dragX + Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(type.name()) && mouseY <= y + panel.dragY + 10;

        int color = hovering ? Colors.getColor(200, 50, 0, 255) : Colors.getColor(127, 140, 141, 255);
        if(isActive) color = Colors.getColor(200, 50, 0, 255);
        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(type.name(), x + panel.dragX - 3, y + panel.dragY, color);

        if(isActive) catePanel.drawCategory(mouseX, mouseY);
    }

    public void cateButtonClicked(float mouseX, float mouseY, int mouseButton) {
        boolean hovering = mouseX >= x + panel.dragX && mouseY >= y + panel.dragY && mouseX <= x + panel.dragX + Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(type.name()) && mouseY <= y + panel.dragY + 10;

        if(hovering) {
            isActive = true;
            for(CategoryButton button : panel.buttons) {
                if(button != this && button.isActive) button.isActive = false;
            }
        }

        if(isActive) catePanel.categoryClicked(mouseX, mouseY, mouseButton);
    }

    public void cateButtonReleased(float mouseX, float mouseY, int state) {
        if(isActive) catePanel.categoryReleased(mouseX, mouseY, state);
    }
}
