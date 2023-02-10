/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 10:58
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CheckBoxComponent extends ChildComponent {

    private String text = "CheckBox";
    private Supplier<Boolean> value;
    private Consumer<Boolean> action;

    public CheckBoxComponent(Component parent, String text, double x, double y, Supplier<Boolean> value, Consumer<Boolean> valueConsumer, Supplier<Boolean> isVisible) {
        super(parent, x, y, FontLoaders.TAHOMA_11.getStringWidth(text) + 8, 5);
        this.text = text;
        this.value = value;
        this.action = valueConsumer;
        setVisible(isVisible);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        double x = getFullX(), y = getFullY(), width = getWidth(), height = getHeight();

        Gui.drawRect(x, y, x + 5, y + 5, SkeetGui.INSTANCE.getColor(0x0C0C0C));

        int color = SkeetGui.INSTANCE.getColor(value.get() ? SkeetGui.INSTANCE.getColor() : 0x242424);

        Gui.drawGradientRect(x + 0.5, y + 0.5, x + 4.5, y + 4.5, color, RenderUtil.darker(color, 0.7f));

        if(SkeetGui.INSTANCE.getAlpha() > 60) FontLoaders.TAHOMA_11.drawString(text, (float) (x + 8), (float) (y + 1.5), SkeetGui.INSTANCE.getColor(0xC9C9C9));

        for(Component component : getChildren()) {
            if(!component.isVisible()) return;
            component.drawComponent(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0 && MouseUtil.isHovered(mouseX, mouseY, getFullX(), getFullY(), getWidth(), getHeight())) {
            action.accept(!value.get());
            return true;
        }
        for(Component component : getChildren()) {
            if(!component.isVisible()) continue;
            if(component.mouseClicked(mouseX, mouseY, mouseButton)) return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        for(Component component : getChildren()) {
            if(!component.isVisible()) continue;
            if(component.keyTyped(typedChar, keyCode)) return true;
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
}
