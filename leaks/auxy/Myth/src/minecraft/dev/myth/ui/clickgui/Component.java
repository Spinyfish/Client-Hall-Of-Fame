/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 19:19
 */
package dev.myth.ui.clickgui;

import dev.myth.api.interfaces.IMethods;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Component implements IMethods {

    @Getter @Setter private double x, y, width, height;
    @Getter private ArrayList<Component> children = new ArrayList<>();
    @Setter private boolean extended = false;

    public void drawComponent(int mouseX, int mouseY) {
        if(getChildren().isEmpty()) return;
        Component previous = null;
        for(Component child : this.getChildren()) {
            if(!child.isVisible()) continue;
            if(previous != null) {
                child.setY(previous.getY() + previous.getFullHeight());
            } else {
                child.setY(this.getY() + this.getHeight());
            }
            child.setX(getX());
            child.setWidth(getWidth());
            child.setHeight(getHeight());
            child.drawComponent(mouseX, mouseY);

            previous = child;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY) {return false;}

    public void guiClosed() {}

    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }

    public boolean isExtendable() {
        return false;
    }

    public boolean isExtended() {
        if(!isExtendable()) {
            return false;
        }
        return extended;
    }

    public double getFullHeight() {
        return height;
    }

    public boolean isVisible() {
        return true;
    }
}
