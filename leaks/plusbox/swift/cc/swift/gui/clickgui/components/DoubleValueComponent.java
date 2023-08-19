/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:44
 */

package cc.swift.gui.clickgui.components;

import cc.swift.gui.clickgui.GuiComponent;
import cc.swift.util.ColorUtil;
import cc.swift.util.MathUtil;
import cc.swift.util.MouseUtil;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import cc.swift.value.impl.DoubleValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class DoubleValueComponent extends GuiComponent {

    private final DoubleValue doubleValue;

    private boolean dragging;

    public DoubleValueComponent(DoubleValue doubleValue, double width, double height) {
        super(0, 0, width, height);
        this.doubleValue = doubleValue;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (dragging) {
            doubleValue.setValue(MathUtil.round(MathHelper.clamp_double((mouseX - x) / width, 0, 1) * (doubleValue.getMaximum() - doubleValue.getMinimum()) + doubleValue.getMinimum(), doubleValue.getIncrement()));
        }

        double value = (doubleValue.getValue() - doubleValue.getMinimum()) / (doubleValue.getMaximum() - doubleValue.getMinimum());

        Gui.drawRect(x, y, x + width, y + height, ColorUtil.LIGHT_GRAY.getRGB());
        Gui.drawRect(x, y + height - 1, x + width, y + height, ColorUtil.DARK_GRAY.getRGB());
        Gui.drawRect(x, y + height - 1, x + width * value, y + height, Color.RED.getRGB());
        Gui.drawRect(x, y, x + width * value, y + height - 1, new Color(255, 0, 0, 70).getRGB());

        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 15);
        int y = (int) Math.ceil(this.y + (height - vietnamFont.getHeight()) / 2);

        vietnamFont.drawStringWithShadow(doubleValue.getName(), x + 6, y, -1);
        vietnamFont.drawStringWithShadow(String.valueOf(doubleValue.getValue()), x + width - 6 - vietnamFont.getStringWidth(String.valueOf(doubleValue.getValue())), y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && MouseUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    @Override
    public void onGuiClosed() {
        dragging = false;
    }

    @Override
    public boolean isVisible() {
        return doubleValue.isVisible();
    }
}
