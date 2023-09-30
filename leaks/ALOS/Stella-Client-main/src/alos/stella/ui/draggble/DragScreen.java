package alos.stella.ui.draggble;

import alos.stella.Stella;
import net.minecraft.util.MathHelper;

public class DragScreen {

    public void draw(int mouseX, int mouseY) {
        for (DragComp draggableComponent : Stella.draggableHUD.getComponents()) {

            if (draggableComponent.allowDraw()) {
                drawComponent(mouseX, mouseY, draggableComponent);
            }
        }
    }

    private void drawComponent(int mouseX, int mouseY, DragComp draggableComponent) {
        boolean hover = MathHelper.isMouseHoveringOnRect(draggableComponent.getX(), draggableComponent.getY(), draggableComponent.getWidth(), draggableComponent.getHeight(), mouseX, mouseY);
        draggableComponent.draw(mouseX, mouseY);
    }

    public void click(int mouseX, int mouseY) {
        for (DragComp draggableComponent : Stella.draggableHUD.getComponents()) {
            if (draggableComponent.allowDraw()) {
                draggableComponent.click(mouseX, mouseY);
            }
        }
    }

    public void release() {
        for (DragComp draggableComponent : Stella.draggableHUD.getComponents()) {
            draggableComponent.release();
        }
    }

}