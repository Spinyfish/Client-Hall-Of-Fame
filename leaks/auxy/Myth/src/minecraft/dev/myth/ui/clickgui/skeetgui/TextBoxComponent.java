/**
 * @project Myth
 * @author CodeMan
 * @at 04.01.23, 12:29
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextBoxComponent extends ChildComponent {

    private String name;
    private Supplier<String> value;
    private Consumer<String> action;

    public TextBoxComponent(Component parent, String name, double x, double y, Supplier<String> getText, Consumer<String> setText, Supplier<Boolean> isVisible) {
        super(parent, x, y, parent.getWidth() - 30, 16);
        this.name = name;
        this.value = getText;
        this.action = setText;
        setVisible(isVisible);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        double x = getFullX(), y = getFullY(), width = getWidth(), height = 10;
        setWidth(getParent().getWidth() - 30);

        boolean isFocused = SkeetGui.INSTANCE.getFocusedComponent() == this;

        Gui.drawRect(x, y + 6, x + width, y + 6 + height, SkeetGui.INSTANCE.getColor(isFocused ? 0xAAAAAA : 0x0C0C0C));
        int color = SkeetGui.INSTANCE.getColor(MouseUtil.isHovered(mouseX, mouseY, x + 0.5, y + 6 + 0.5, width - 0.5, height - 0.5) ? 0x292929 : 0x202020);
        Gui.drawGradientRect(x + 0.5, y + 6 + 0.5, x + width - 0.5, y + 6 + height - 0.5, color, RenderUtil.darker(color, 1.2f));

        FontLoaders.TAHOMA_11.drawString(name, (float) (x + 2), (float) (y + 1.5), SkeetGui.INSTANCE.getColor(0xC9C9C9));
        FontLoaders.TAHOMA_11.drawString(value.get() + (isFocused ? "|" : ""), (float) (x + 5), (float) (y + 10), SkeetGui.INSTANCE.getColor(0x959595));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && MouseUtil.isHovered(mouseX, mouseY, getFullX(), getFullY() + 6, getWidth(), 10)) {
            SkeetGui.INSTANCE.setFocusedComponent(this);
            return true;
        } else {
            if(SkeetGui.INSTANCE.getFocusedComponent() == this) {
                SkeetGui.INSTANCE.setFocusedComponent(null);
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {

        if(SkeetGui.INSTANCE.getFocusedComponent() == this) {
            boolean isValid = Character.isLetterOrDigit(typedChar) || Character.isSpaceChar(typedChar);
            if(isValid && value.get().length() < 30) action.accept(value.get() + typedChar);
            if(keyCode == Keyboard.KEY_BACK && value.get().length() > 0) action.accept(value.get().substring(0, value.get().length() - 1));
            if(keyCode == Keyboard.KEY_RETURN) SkeetGui.INSTANCE.setFocusedComponent(null);
        }

        return false;
    }

    @Override
    public void guiClosed() {
        if(SkeetGui.INSTANCE.getFocusedComponent() == this) {
            SkeetGui.INSTANCE.setFocusedComponent(null);
        }
    }
}
