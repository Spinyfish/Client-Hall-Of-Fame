/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 10:55
 */
package dev.myth.ui.clickgui.skeetgui;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.myth.api.utils.font.CFontRenderer;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;

public class GroupBoxComponent extends ChildComponent {

    private String name = "GroupBox";

    public GroupBoxComponent(Component parent, String name, double x, double y, double width, double height) {
        super(parent, x, y, width, height);
        this.name = name;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        double x = getFullX();
        double y = getFullY();

        Gui.drawRect(x, y, x + getWidth(), y + getHeight(), SkeetGui.INSTANCE.getColor(0x0C0C0C));
        Gui.drawRect(x + 0.5, y + 0.5, x + getWidth() - 0.5, y + getHeight() - 0.5, SkeetGui.INSTANCE.getColor(0x242424));
        Gui.drawRect(x + 1, y + 1, x + getWidth() - 1, y + getHeight() - 1, SkeetGui.INSTANCE.getColor(0x171717));

        CFontRenderer fontRenderer = FontLoaders.TAHOMA_11;

        double textWidth = fontRenderer.getStringWidth(ChatFormatting.BOLD + name);

        Gui.drawRect(x + 4, y, x + textWidth + 8, y + 1, SkeetGui.INSTANCE.getColor(0x171717));

        if (SkeetGui.INSTANCE.getAlpha() > 60)
            fontRenderer.drawOutlinedString(ChatFormatting.BOLD + name, (float) (x + 5), (float) (y), SkeetGui.INSTANCE.getColor(0xC9C9C9), 0x78000000);

        ChildComponent prev = null;
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            if (prev != null) {
                component.setY(prev.getY() + prev.getHeight() + 3);
            } else {
                component.setY(7);
            }
            component.drawComponent(mouseX, mouseY);
            prev = (ChildComponent) component;
        }
        if (prev != null) {
            setHeight(prev.getY() + prev.getHeight() + 5);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            if (component.mouseClicked(mouseX, mouseY, mouseButton)) return true;
        }
        return false;
    }

    @Override
    public double getFullHeight() {
        double height = getHeight();
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            height += component.getFullHeight();
        }
        return height;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            if (component.keyTyped(typedChar, keyCode)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            if(component.mouseReleased(mouseX, mouseY)) return true;
        }
        return false;
    }

    @Override
    public void guiClosed() {
        for (Component component : getChildren()) {
            if (!component.isVisible()) continue;
            component.guiClosed();
        }
        super.guiClosed();
    }
}
