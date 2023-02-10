/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 15:25
 */
package dev.myth.ui.clickgui.skeetgui;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderComponent extends ChildComponent {

    private boolean sliding;
    private Supplier<Double> value, min, max, inc;
    private Consumer<Double> setValue;
    private Supplier<String> valueText;
    private String text;

    public SliderComponent(Component parent, String text, double x, double y, Supplier<Double> value, Consumer<Double> setValue, Supplier<String> valueText, Supplier<Double> min, Supplier<Double> max, Supplier<Double> inc, Supplier<Boolean> isVisible) {
        super(parent, x, y, parent.getWidth() - 30, 12);
        this.text = text;
        this.value = value;
        this.valueText = valueText;
        this.setValue = setValue;
        this.min = min;
        this.max = max;
        this.inc = inc;
        setVisible(isVisible);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        setWidth(getParent().getWidth() - 30);
        double fullX = getFullX(), fullY = getFullY(), width = getWidth(), height = getHeight();
        double x = fullX + 2;
        double sliderWidth = this.getWidth() - 4;
        if (sliding) {
            double value = (mouseX - x) / sliderWidth;
            if (value < 0) value = 0;
            if (value > 1) value = 1;
            setValue.accept(MathUtil.round(value * (max.get() - min.get()) + min.get(), inc.get()));
        }

        double yOffset = 6, sliderThickness = 4;

        Gui.drawRect(x, fullY + yOffset, x + sliderWidth + 1, fullY + yOffset + sliderThickness, SkeetGui.INSTANCE.getColor(0x0C0C0C));
        Gui.drawRect(x + 0.5, fullY + yOffset + 0.5, x + 0.5 + sliderWidth, fullY + yOffset + sliderThickness - 0.5, SkeetGui.INSTANCE.getColor(0x3A3A3A));
        Gui.drawRect(x + 0.5, fullY + yOffset + 0.5, x + 0.5 + sliderWidth * (value.get() - min.get()) / (max.get() - min.get()), fullY + yOffset + sliderThickness - 0.5, SkeetGui.INSTANCE.getColor(SkeetGui.INSTANCE.getColor()));

        Gui.drawGradientRect(x + 0.5, fullY + yOffset + 0.5, x + 0.5 + sliderWidth, fullY + yOffset + sliderThickness - 0.5, SkeetGui.INSTANCE.getColor(0x3A3A3A), RenderUtil.darker(SkeetGui.INSTANCE.getColor(0x3A3A3A), 0.7f));
        Gui.drawGradientRect(x + 0.5, fullY + yOffset + 0.5, x + 0.5 + sliderWidth * (value.get() - min.get()) / (max.get() - min.get()), fullY + yOffset + sliderThickness - 0.5, SkeetGui.INSTANCE.getColor(SkeetGui.INSTANCE.getColor()), RenderUtil.darker(SkeetGui.INSTANCE.getColor(SkeetGui.INSTANCE.getColor()), 0.7f));

        if (SkeetGui.INSTANCE.getAlpha() > 60) {
            FontLoaders.TAHOMA_11.drawString(text, (float) (fullX + 2), (float) (fullY + 1.5), SkeetGui.INSTANCE.getColor(0xC9C9C9));
            FontLoaders.TAHOMA_11.drawOutlinedString(ChatFormatting.BOLD + valueText.get(), (float) (x + 0.5 + sliderWidth * (value.get() - min.get()) / (max.get() - min.get()) - FontLoaders.TAHOMA_11.getStringWidth(ChatFormatting.BOLD + valueText.get()) / 2F), (float) (fullY + yOffset + sliderThickness), SkeetGui.INSTANCE.getColor(0xC9C9C9), 0x78000000);
            FontLoaders.TAHOMA_9.drawString("-", (float) (fullX), (float) (fullY + yOffset + 2), SkeetGui.INSTANCE.getColor(0x707070));
            FontLoaders.TAHOMA_9.drawString("+", (float) (fullX + width), (float) (fullY + yOffset + 2), SkeetGui.INSTANCE.getColor(0x707070));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (MouseUtil.isHovered(mouseX, mouseY, getFullX() + 2, getFullY() + 6, getWidth() - 4, 6)) {
                sliding = true;
                return true;
            }
            if(MouseUtil.isHovered(mouseX, mouseY, getFullX() - 4, getFullY() + 6, 5, 6)) {
                setValue.accept(MathUtil.round(Math.max(value.get() - inc.get(), min.get()), inc.get()));
                return true;
            }
            if(MouseUtil.isHovered(mouseX, mouseY, getFullX() + getWidth() - 1, getFullY() + 6, 5, 6)) {
                setValue.accept(MathUtil.round(Math.min(value.get() + inc.get(), max.get()), inc.get()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        sliding = false;
        return false;
    }

    @Override
    public void guiClosed() {
        sliding = false;
        super.guiClosed();
    }
}
