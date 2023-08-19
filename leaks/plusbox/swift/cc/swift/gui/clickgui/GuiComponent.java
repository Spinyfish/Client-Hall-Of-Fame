/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:07
 */

package cc.swift.gui.clickgui;

import cc.swift.util.IMinecraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
public class GuiComponent implements IMinecraft {
    @Getter @Setter
    protected double x, y, width, height;

    @Getter
    private final ArrayList<GuiComponent> children = new ArrayList<>();

    public void draw(int mouseX, int mouseY) {
        GuiComponent lastComponent = null;
        for (GuiComponent component : getChildren()) {
            if (!component.isVisible()) continue;

            if (lastComponent != null)
                component.setY(lastComponent.getY() + lastComponent.getFullHeight());
            else
                component.setY(y + height);

            component.setX(x + width / 2 - component.getWidth() / 2);

            component.draw(mouseX, mouseY);
            lastComponent = component;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        children.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY) {
        children.forEach(panel -> panel.mouseReleased(mouseX, mouseY));
    }

    public void keyTyped(char typedChar, int keyCode) {
        children.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }

    public void onGuiClosed() {
        children.forEach(GuiComponent::onGuiClosed);
    }

    public double getFullHeight() {
        return this.getHeight();
    }

    public boolean isVisible() {
        return true;
    }
}
