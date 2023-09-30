package club.pulsive.client.ui.clickgui.clickgui.component.implementations;

import club.pulsive.client.ui.clickgui.clickgui.component.SettingComponent;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ColorPickerComponent extends SettingComponent<ColorProperty> {

    private boolean pickerSliding, hueSliding;
    private float hue, saturation, brightness;

    public ColorPickerComponent(ColorProperty setting, float x, float y, float width, float height) {
        super(setting, x, y, width, height);
    }

    public ColorPickerComponent(ColorProperty setting, float x, float y, float width, float height, boolean visible) {
        super(setting, x, y, width, height, visible);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (!visible) return;
        y += 3;
        Gui.drawRect(x, y - 5, x + width, y + height + 2 - 3, RenderUtil.applyOpacity(new Color(ClientSettings.mainColor.getValue().getRGB()).darker().darker(), 0.5f).getRGB());
        Color color = setting.getValue();
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float startX = x + 4;
        float startY = y + 2;
        float h = height - 8;
        float w = width - 25;
        float selectorWidth = 4;
        float selectorHeight = 4;
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        float hueHeight = MathHelper.clamp_float(hsb[0] * h, selectorWidth / 2, h - selectorWidth / 2);
        float saturationWidth = MathHelper.clamp_float(hsb[1] * w, -selectorWidth, w - selectorWidth);
        float brightnessHeight = MathHelper.clamp_float(hsb[2] * h, selectorHeight, h + selectorHeight);
        if(hueSliding) {
            hue = MathHelper.clamp_float((mouseY - startY) / h, 0, 1);
            hueHeight = MathHelper.clamp_float(hue * h, selectorWidth / 2, h - selectorWidth / 2);
            setting.setValue(new Color(Color.HSBtoRGB(MathHelper.clamp_float(hue, 0.01f, 0.999f), saturation, brightness)));
        }
        if (pickerSliding) {
            saturation = MathHelper.clamp_float((mouseX - startX) / w, 0, 1);
            brightness = MathHelper.clamp_float(1 - ((mouseY - startY) / h), 0, 1);
            saturationWidth = MathHelper.clamp_float(saturation * w, -selectorWidth, w - selectorWidth);
            brightnessHeight = MathHelper.clamp_float(brightness * h, selectorHeight, h + selectorHeight);
            setting.setValue(new Color(Color.HSBtoRGB(hue, MathHelper.clamp_float(saturation, 0.01f, 0.999f), MathHelper.clamp_float(brightness, 0.01f, 0.999f))));
        }
        for(int i = 0; i < h; i++) {
            float yPos = startY + i;
            Gui.drawRect(x + width - 18, yPos, x + width - 9, yPos + 1, Color.getHSBColor(i / h, 1, 1).getRGB());
        }
        theme.drawColorPickerComponent(this, startX, startY, w, h);
        RenderUtil.drawRect(startX + saturationWidth, startY + h - brightnessHeight, startX + saturationWidth + selectorWidth, startY + h - brightnessHeight + selectorWidth, -1);
        RenderUtil.drawRect(x + width - 18, startY + hueHeight - selectorHeight / 2, x + width - 9, startY + hueHeight + selectorHeight / 2, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float startY = y + 2;
        float startX = x + 4;
        float w = width - 25;
        float h = height - 8;
        if (visible && !pickerSliding && mouseButton == 0 && RenderUtil.isHovered(startX, startY, w, h, mouseX, mouseY))
            pickerSliding = true;
        if (visible && !hueSliding && mouseButton == 0 && RenderUtil.isHovered(x + width - 18, startY, x + width - 9, h, mouseX, mouseY))
            hueSliding = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        pickerSliding = false;
        hueSliding = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
