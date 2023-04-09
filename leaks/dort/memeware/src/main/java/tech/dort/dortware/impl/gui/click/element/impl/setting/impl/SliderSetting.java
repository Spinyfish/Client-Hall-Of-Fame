package tech.dort.dortware.impl.gui.click.element.impl.setting.impl;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.gui.click.GuiUtils;
import tech.dort.dortware.impl.gui.click.element.impl.setting.Setting;
import tech.dort.dortware.impl.modules.render.ClickGUI;

import java.awt.*;
import java.text.DecimalFormat;

public class SliderSetting extends Setting {

    private final DecimalFormat format = new DecimalFormat("0.##");
    private int startPos;
    private int endPos;
    private int sliderHeight;
    private final NumberValue numberValue;
    private boolean selected;

    public SliderSetting(Value<?> value, int width) {
        this.value = value;
        this.numberValue = (NumberValue) value;
        this.width = width;
        this.height = 16;

    }

    @Override
    public void onDraw(int mouseX, int mouseY, float partialTicks) {
        final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Small").getRenderer();
        startPos = posX + SETTING_PADDING;
        endPos = posX + width - SETTING_PADDING;
        sliderHeight = posY + height - 4;
        ClickGUI clickGUI = Client.INSTANCE.getModuleManager().get(ClickGUI.class);
        int color = clickGUI.getGuiColor();
        int diff = endPos - startPos;
        String value = format.format(numberValue.getValue());
        double length = (numberValue.getValue() - numberValue.getMin()) / (numberValue.getMax() - numberValue.getMin()) * diff;

        GuiUtils.drawStraightLine(startPos, endPos, sliderHeight, 2, new Color(120, 120, 120).getRGB());
        GuiUtils.drawStraightLine(startPos, (int) (startPos + length), sliderHeight, 2, color);
        String valueStr = value + (numberValue.hasUnit() ? " " + numberValue.getUnit().getDisplayText() : "");
        GuiUtils.glStartFontRenderer();
        font.drawString(this.value.getName(), startPos, sliderHeight - font.getHeight(this.value.getName()) - 4, -1);
        font.drawString(valueStr, endPos - font.getWidth(valueStr), sliderHeight - font.getHeight(this.value.getName()) - 4, -1);
        GuiUtils.glResets();


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        moveSlider(mouseX, mouseY, mouseButton, false);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        selected = false;
    }

    @Override
    public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick, boolean otherSelected) {
        return moveSlider(mouseX, mouseY, clickedMouseButton, otherSelected);
    }

    private boolean moveSlider(int mouseX, int mouseY, int clickedMouseButton, boolean otherSelected) {
        double diff = endPos - startPos;
        double length = endPos - mouseX;
        if (length < 0) {
            length = 0;
        }
        if (length > diff) {
            length = diff;
        }
        if (selected || GuiUtils.isHovering(mouseX, mouseY, startPos, sliderHeight - 2, endPos - startPos, 4) && !otherSelected) {
            selected = true;
            double value = numberValue.getMax() - ((diff * numberValue.getMin()) - (length * numberValue.getMin()) + (length * numberValue.getMax())) / diff + numberValue.getMin();
            numberValue.setValueAutoSave(value);
        }

        return selected;
    }
}
