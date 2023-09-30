package de.fanta.clickgui.defaultgui.components;

import de.fanta.Client;
import de.fanta.setting.Setting;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;

public class Slider {
    public float x;
    public float y;
    private CategoryPanel panel;
    private float dragX;
    private Setting setting;
    private boolean isDragged;

    public Slider(Setting setting, CategoryPanel panel, float x, float y) {
        this.setting = setting;
        this.panel = panel;
        this.x = x;
        this.y = y;
    }

    public void drawSlider(float mouseX, float mouseY) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        de.fanta.setting.settings.Slider slider = (de.fanta.setting.settings.Slider) setting.getSetting();

        double percent = dragX / 100;
        double value = getIncremental(percent * 100 * ((Double)slider.getMaxValue() - (Double)slider.getMinValue()) / 100 + (Double)slider.getMinValue(), slider.getStepValue());

        value = Math.min((Double)value, (Double)slider.getMaxValue());
        value = Math.max((Double)value, (Double)slider.getMinValue());

        if(isDragged) {
            dragX = mouseX - (x + xOff + 5);
            slider.curValue = value;
        }

        float sliderX = (float) (((Double)slider.curValue - (Double)slider.getMinValue()) / ((Double)slider.getMaxValue() - (Double)slider.getMinValue()) * 100);

        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(setting.getName(), (int) (x + xOff) + 110, (int) (y + yOff) - 2, Colors.getColor(255, 255, 255, 255));
        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(String.valueOf(slider.curValue), (int) (x + xOff) + 106 / 2 -  Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(String.valueOf(slider.curValue)) / 2, (int) (y + yOff) + 8, -1);

        RenderUtil.rectangle(x + xOff + 4, y + yOff + 2, x + xOff + 106, y + yOff + 9, Colors.getColor(0, 0, 0, 255));
        RenderUtil.rectangle(x + xOff + 5, y + yOff + 3, x + xOff + 105, y + yOff + 8, Colors.getColor(75, 75, 75, 255));
        RenderUtil.rectangle(x + xOff + 5, y + yOff + 3, x + xOff + sliderX + 5, y + yOff + 8, Colors.getColor(200, 50, 0, 255));
    }

    public void sliderClicked(float mouseX, float mouseY, float mouseButton) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff + 5 && mouseY >= y + yOff + 3 && mouseX <= x + xOff + 105 && mouseY <= y + yOff + 8 && mouseButton == 0;

        if(hovering)
            isDragged = true;
    }

    public void sliderReleased(float mouseX, float mouseY, float state) {
        if(isDragged) isDragged = false;
    }

    private double getIncremental(double val, double inc) {
        double one = 1.0 / inc;
        return (double)Math.round(val * one) / one;
    }
}
