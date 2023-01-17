package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.ColorValue;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2f;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ColorValueComponent extends ValueComponent {

    private final static double COLOR_WIDTH = 5;

    private boolean selected = false;
    private boolean colorPickerDown, huePickerDown;
    private float offset = 10, edge = 0.5f;
    private float huePointer = 0;
    private double pickerWidth = 90, pickerHeight = 100;
    private Vector2f pointer = new Vector2f(1, 1);
    private Color hueSelectorColor = Color.RED;

    public ColorValueComponent(final Value<?> value) {
        super(value);
    }

    @Override
    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.position = position;

        pickerWidth = 105;
        pickerHeight = 120;

        // Cast
        final ColorValue colorValue = (ColorValue) this.value;

        Color value = colorValue.getValue();
        final float valueWidth = this.nunitoSmall.width(this.value.getName()) + 4;

        this.nunitoSmall.drawString(this.value.getName(), this.position.x, this.position.y, this.getStandardClickGUI().fontDarkColor.hashCode());

        RenderUtil.roundedRectangle(this.position.x + valueWidth, this.position.y, COLOR_WIDTH * 2, COLOR_WIDTH, COLOR_WIDTH / 2.0F, colorValue.getValue());
        this.height = 14;

        if (selected) {

            this.height = 120;

//            getStandardClickGUI().overlayPresentlayPresent = this;

//            NORMAL_POST_RENDER_RUNNABLES.add(() -> {
            double x = this.position.x + edge + offset + valueWidth + 4, y = this.position.y + edge;

            // Main Panel Shadow
            RenderUtil.dropShadow(10, (float) x, (float) y, (float) pickerWidth, (float) pickerHeight,
                    40, getStandardClickGUI().round * 2);

            // Main Panel Border
            RenderUtil.roundedRectangle(x - edge, this.position.y, pickerWidth, pickerHeight,
                    getStandardClickGUI().round, getStandardClickGUI().sidebarColor);

            // Main Panel
            RenderUtil.roundedRectangle(x, y, pickerWidth - edge * 2, pickerHeight - edge * 2,
                    getStandardClickGUI().round, getStandardClickGUI().backgroundColor);

            double pickerHeight = this.pickerHeight * 0.55;

            // Main Color Gradient
            RenderUtil.drawRoundedGradientRect(x, y, pickerWidth - edge * 2, pickerHeight, getStandardClickGUI().round, Color.WHITE, hueSelectorColor, false);
            RenderUtil.drawRoundedGradientRect(x - 0.5, y, pickerWidth - edge * 2 + 1, pickerHeight + 0.5, 0.5, Color.BLACK, new Color(0, 0, 0, 0), true);
//                RenderUtil.rectangle(x, y + pickerHeight - 2, pickerWidth - edge * 2, 5, Color.BLACK);

            double padding = 8.5f;

            double huePickerX = x + padding;
            double huePickerY = y + padding + pickerHeight;
            double huePickerWidth = pickerWidth - padding * 2;

            // Hue Selector
            RenderUtil.roundedRectangle(huePickerX, huePickerY + 0.5, 20, getStandardClickGUI().round - 1.5, 2.5F, Color.RED);
            RenderUtil.roundedRectangle(huePickerX + huePickerWidth - 20, huePickerY + 0.5, 20, getStandardClickGUI().round - 1.5, 2.5F, Color.RED);

            RenderUtil.rainbowRectangle(huePickerX, huePickerY + 0.5, huePickerWidth, getStandardClickGUI().round - 1);

            // Color Preview
            RenderUtil.roundedRectangle(x + padding, y + pickerHeight + padding + padding + getStandardClickGUI().round - 1, 15, 22.5f,
                    7 / 2f, colorValue.getValue());

            if (colorPickerDown) {
                pointer = new Vector2f((float) (mouseX - x), (float) (mouseY - y));

                pointer.x = MathHelper.clamp_float(pointer.x, 0f, (float) pickerWidth);
                pointer.y = MathHelper.clamp_float(pointer.y, 0f, (float) pickerHeight);

                Color color = ColorUtil.mixColors(new Color(0, 0, 0, 0), ColorUtil.mixColors(
                        hueSelectorColor, Color.WHITE, pointer.x / pickerWidth), pointer.y / pickerHeight);

                colorValue.setValue(color);
            } else if (huePickerDown) {
                huePointer = (float) (mouseX - huePickerX);
                huePointer = MathHelper.clamp_float(huePointer, (float) 0, (float) huePickerWidth);
                hueSelectorColor = Color.getHSBColor((float) (huePointer / huePickerWidth), 1, 1);

                Color color = ColorUtil.mixColors(new Color(0, 0, 0, 0), ColorUtil.mixColors(
                        hueSelectorColor, Color.WHITE, pointer.x / pickerWidth), pointer.y / pickerHeight);

                colorValue.setValue(color);
            }

            // Selected Hue Marker
            RenderUtil.roundedRectangle(huePickerX + huePointer - getStandardClickGUI().round / 2f - 0.5f, huePickerY - 0.5f, getStandardClickGUI().round + 1, getStandardClickGUI().round + 1, getStandardClickGUI().round / 2f, Color.WHITE);
            RenderUtil.roundedRectangle(huePickerX + huePointer - getStandardClickGUI().round / 2f, huePickerY, getStandardClickGUI().round, getStandardClickGUI().round, getStandardClickGUI().round / 2f, Color.BLACK);
            RenderUtil.roundedRectangle(huePickerX + huePointer - getStandardClickGUI().round / 2f + 0.5f, huePickerY + 0.5f, getStandardClickGUI().round - 1, getStandardClickGUI().round - 1, getStandardClickGUI().round / 2f, hueSelectorColor);

            if (pointer.x != -1 && pointer.y != -1) {
                RenderUtil.roundedRectangle(x - 1 + pointer.x - COLOR_WIDTH / 2, y - 1 + pointer.y - COLOR_WIDTH / 2, COLOR_WIDTH + 2,
                        COLOR_WIDTH + 2, COLOR_WIDTH / 2.0F + 1, Color.WHITE);

                RenderUtil.roundedRectangle(x - 0.5f + pointer.x - COLOR_WIDTH / 2, y - 0.5f + pointer.y - COLOR_WIDTH / 2, COLOR_WIDTH + 1,
                        COLOR_WIDTH + 1, COLOR_WIDTH / 2.0F + 0.5, Color.BLACK);

                RenderUtil.roundedRectangle(x + pointer.x - COLOR_WIDTH / 2, y + pointer.y - COLOR_WIDTH / 2, COLOR_WIDTH, COLOR_WIDTH,
                        COLOR_WIDTH / 2.0F, colorValue.getValue());
            }

            Color color = colorValue.getValue();

            double textX = x + padding * 2 + 15;
            double textY = y + pickerHeight + padding + padding + getStandardClickGUI().round - 1 + 3;

            FontManager.getNunito(17).drawCenteredString(color.getRed() + "", textX + padding, textY, getStandardClickGUI().fontDarkColor.hashCode());
            FontManager.getNunito(17).drawCenteredString(color.getGreen() + "", textX + 30, textY, getStandardClickGUI().fontDarkColor.hashCode());
            FontManager.getNunito(17).drawCenteredString(color.getBlue() + "", textX + padding * 6, textY, getStandardClickGUI().fontDarkColor.hashCode());

            textY += 13;

            FontManager.getNunito(13).drawString(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()), textX, textY, new Color(55, 59, 61).hashCode());
//            });
        }
    }

    @Override
    public void click(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.position == null) {
            return;
        }

        final float valueWidth = this.nunitoSmall.width(this.value.getName()) + 4;

        colorPickerDown = selected && GUIUtil.mouseOver(this.position.x + offset + valueWidth, this.position.y, pickerWidth, pickerHeight * 0.55, mouseX, mouseY);
        huePickerDown = selected && GUIUtil.mouseOver(this.position.x + offset + valueWidth, this.position.y + pickerHeight * 0.55, pickerWidth, 20, mouseX, mouseY);

        double x = this.position.x + 14.5 + valueWidth, y = this.position.y + edge;
        double textX = x + 32;
        double textY = y + pickerHeight - 40 + getStandardClickGUI().round;

        if (GUIUtil.mouseOver(textX, textY, 60, 10, mouseX, mouseY)) {
            Color color = (Color) this.value.getValue();
            GuiScreen.setClipboardString(color.getRed() + ", " + color.getBlue() + ", " + color.getGreen());
        } else if (GUIUtil.mouseOver(textX, textY + 13, 60, 10, mouseX, mouseY)) {
            Color color = (Color) this.value.getValue();
            GuiScreen.setClipboardString(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
        }

        selected = (getStandardClickGUI().overlayPresent == null || selected) &&
                (
                        colorPickerDown ||
                                GUIUtil.mouseOver(this.position.x + offset + valueWidth, this.position.y + pickerHeight * 0.55, pickerWidth, 52, mouseX, mouseY) ||
                                (!selected && GUIUtil.mouseOver(position.x, this.position.y - 3.5f, getStandardClickGUI().width - 70, this.height, mouseX, mouseY))
                );
    }

    @Override
    public void released() {
        colorPickerDown = huePickerDown = false;
    }

    @Override
    public void bloom() {
    }

    @Override
    public void key(final char typedChar, final int keyCode) {
        if (this.position == null) {
            return;
        }
    }
}
