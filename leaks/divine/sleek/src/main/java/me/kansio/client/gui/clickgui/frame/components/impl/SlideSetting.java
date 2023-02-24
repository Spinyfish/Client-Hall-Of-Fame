package me.kansio.client.gui.clickgui.frame.components.impl;

import me.kansio.client.gui.clickgui.frame.Values;
import me.kansio.client.gui.clickgui.frame.components.Component;
import me.kansio.client.gui.clickgui.frame.components.FrameModule;
import me.kansio.client.value.Value;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SlideSetting extends Component implements Values {
    public SlideSetting(int x, int y, FrameModule owner, Value setting) {
        super(x, y, owner, setting);
    }

    private boolean drag;

    @Override
    public void initGui() {
        drag = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (!Mouse.isButtonDown(0)) drag = false;

        NumberValue slide = (NumberValue) getSetting();
        double min = slide.getMin().doubleValue();
        double max = slide.getMax().doubleValue();
        double diff = Math.min(defaultWidth + 5, Math.max(0, mouseX - (this.x)));
        double renderWidth = defaultWidth * (slide.getValue().doubleValue() - min) / (max - min);
        Gui.drawRect(x, y, x + (int) renderWidth, y + getOffset(), headerColor);

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        if (drag) {
            if (diff == 0)
                slide.setValue(min);
            else {
                double newValue = roundToPlace((diff / defaultWidth) * (max - min) + min, 2);
                if (newValue <= max)
                    this.setValue(newValue);
            }
        }

        fontRenderer.drawString("§7" + getSetting().getName() + ": §f" + roundToPlace(((NumberValue) getSetting()).getValue().doubleValue(), 2), x + 5, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)), stringColor, true);
    }



    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private double snapToStep(double value, double valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * (double) Math.round(value / valueStep);

        return value;
    }

    private void setValue(Number value) {
        final NumberValue set = (NumberValue) getSetting();
        if (set.getIncrement() instanceof Double) {
            set.setValue(MathHelper.clamp_double(snapToStep(value.doubleValue(), set.getIncrement().doubleValue()), set.getMin().doubleValue(), set.getMax().doubleValue()));//change value step?
        }
        if (set.getIncrement() instanceof Integer) {
            set.setValue(MathHelper.clamp_int((int) snapToStep(value.doubleValue(), set.getIncrement().doubleValue()), set.getMin().intValue(), set.getMax().intValue()));//change value step?
        }
        if (set.getIncrement() instanceof Float) {
            set.setValue(MathHelper.clamp_float((float) snapToStep(value.doubleValue(), set.getIncrement().doubleValue()), set.getMin().floatValue(), set.getMax().floatValue()));//change value step?
        }
        if (set.getIncrement() instanceof Long) {
            set.setValue(MathHelper.clamp_int((int) snapToStep(value.doubleValue(), set.getIncrement().doubleValue()), set.getMin().intValue(), set.getMax().intValue()));//change value step?
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return drag = RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public int getOffset() {
        return 15;
    }
}
