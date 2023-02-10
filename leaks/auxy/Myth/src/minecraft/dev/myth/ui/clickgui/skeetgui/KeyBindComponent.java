/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 12:18
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyBindComponent extends ChildComponent {

    private Supplier<Integer> bind;
    private Consumer<Integer> setBind;
    private boolean binding;

    public KeyBindComponent(Component parent, double x, double y, Supplier<Integer> bind, Consumer<Integer> setBind, Supplier<Boolean> visible) {
        super(parent, x, y, FontLoaders.SMALLEST_PIXEL.getStringWidth("[-]"), 8);
        this.bind = bind;
        this.setBind = setBind;
        setVisible(visible);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        String bind = binding ? "..." : this.bind.get() == 0 ? "-" : Keyboard.getKeyName(this.bind.get());
        double textWidth = FontLoaders.SMALLEST_PIXEL.getStringWidth("[" + bind + "]");

        setX(((ChildComponent)getParent()).getParent().getWidth() - textWidth - 10);
        setWidth(textWidth);

        double x = getFullX(), y = getFullY();
        if(SkeetGui.INSTANCE.getAlpha() > 60) FontLoaders.SMALLEST_PIXEL.drawOutlinedString("[" + bind + "]", (float) x, (float) y + 2, SkeetGui.INSTANCE.getColor(0x707070), SkeetGui.INSTANCE.getColor(0x000000));
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if(binding) {
            if(keyCode != Keyboard.KEY_ESCAPE) {
                setBind.accept(keyCode);
            } else {
                setBind.accept(0);
            }
            binding = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0 && MouseUtil.isHovered(mouseX, mouseY, getFullX(), getFullY(), getWidth(), getHeight())) {
            binding = true;
            return true;
        }
        return false;
    }
}
