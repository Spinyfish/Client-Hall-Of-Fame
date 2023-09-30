package de.fanta.clickgui.defaultgui;

import java.util.ArrayList;

import de.fanta.clickgui.defaultgui.components.CategoryButton;
import de.fanta.module.Module;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;

public class Panel {
    private float x, y;
    public float dragX, dragY;
    private float lastDragX, lastDragY;
    public boolean isDragged;
    public ArrayList<CategoryButton> buttons;

    public Panel(float x, float y) {
        this.x = x;
        this.y = y;

        buttons = new ArrayList<>();

        float moduleY = 5;

        for(Module.Type type : Module.Type.values()) {
            buttons.add(new CategoryButton(type, this, x + 3, y + moduleY));
            moduleY += 15;
        }
    }

    public void drawPanel(float mouseX, float mouseY) {
        if(this.isDragged) {
            this.dragX = mouseX - lastDragX;
            this.dragY = mouseY - lastDragY;
        }

        RenderUtil.rectangle(x + dragX, y + dragY, x + dragX + 325, y + dragY + 215, Colors.getColor(22, 22, 22, 255));
        RenderUtil.rectangle(x + dragX, y + dragY, x + dragX + 325, y + dragY + 3, Colors.getColor(17, 17, 17, 255));
        RenderUtil.rectangle(x + dragX + 50, y + dragY + 3, x + dragX + 51, y + dragY + 215, Colors.getColor(100, 100, 100, 255));

        buttons.forEach(button -> {
            button.drawCateButton(mouseX, mouseY);

            if(button.isActive) {
                float xOff = button.catePanel.lengthModule + 25;
                if(xOff > 25) RenderUtil.rectangle(x + dragX + xOff + 48, y + dragY + 3, x + dragX + xOff + 49, y + dragY + 215, Colors.getColor(100, 100, 100, 255));
            }
        });
    }

    public void panelClicked(float mouseX, float mouseY, int mouseButton) {
        boolean hovering = mouseX >= x + dragX && mouseY >= y + dragY && mouseX <= x + dragX + 325 && mouseY <= y + dragY + 3;

        if(hovering && mouseButton == 0) {
            this.isDragged = true;
            this.lastDragX = mouseX - dragX;
            this.lastDragY = mouseY - dragY;
        }

        buttons.forEach(button -> button.cateButtonClicked(mouseX, mouseY, mouseButton));
    }

    public void panelReleased(float mouseX, float mouseY, int state) {
        if(this.isDragged) this.isDragged = false;

        buttons.forEach(button -> button.cateButtonReleased(mouseX, mouseY, state));
    }

    public void panelHandleInput() {
        buttons.forEach(button -> button.catePanel.categoryHandleInput());
    }
}
