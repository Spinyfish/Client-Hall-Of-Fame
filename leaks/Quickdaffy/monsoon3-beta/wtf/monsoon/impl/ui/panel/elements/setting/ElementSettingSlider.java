/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.impl.ui.panel.elements.setting;

import java.awt.Color;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.primitive.Click;

public class ElementSettingSlider
extends ElementSetting<Number> {
    private boolean dragging = false;

    public ElementSettingSlider(Setting<Number> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        super.draw(mouseX, mouseY, mouseDelta);
        if (!Mouse.isButtonDown((int)0)) {
            this.dragging = false;
        }
        float offset = 4.0f;
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(this.getX() + 1.0f, this.getY(), this.getWidth() - 2.0f, this.getHeight(), new Color(0x252525));
        Wrapper.getFontUtil().productSansSmall.drawString(this.getSetting().getName(), this.getX() + 5.0f, this.getY() + 2.0f, new Color(180, 180, 180), false);
        float sliderWidth = this.getWidth() - offset * 2.0f;
        float diff = Math.min(sliderWidth, Math.max(0.0f, mouseX - (this.getX() + offset)));
        float min = ((Number)this.getSetting().getMinimum()).floatValue();
        float max = ((Number)this.getSetting().getMaximum()).floatValue();
        float step = ((Number)this.getSetting().getIncrementation()).floatValue();
        float current = ((Number)this.getSetting().getValue()).floatValue();
        float renderWidth = sliderWidth * (current - min) / (max - min);
        Wrapper.getFontUtil().productSansSmaller.drawString("" + current, this.getX() + this.getWidth() - 4.0f - (float)Wrapper.getFontUtil().productSansSmaller.getStringWidth("" + current), this.getY() + 2.0f, new Color(180, 180, 180), false);
        RoundedUtils.round(this.getX() + offset, this.getY() + this.getHeight() - 3.0f, sliderWidth, 2.0f, 1.0f, new Color(0x1C1C1C));
        RoundedUtils.round(this.getX() + offset, this.getY() + this.getHeight() - 3.0f, renderWidth, 2.0f, 1.0f, ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        if (this.dragging) {
            float finalValue;
            float value = (float)MathUtils.round(diff / sliderWidth * (max - min) + min, 2.0);
            value = (float)Math.round(Math.max(min, Math.min(max, value)) * (1.0f / step)) / (1.0f / step);
            float f = finalValue = diff == 0.0f ? min : value;
            if (this.getSetting().getValue() instanceof Double) {
                this.getSetting().setValue(Double.valueOf(finalValue));
            } else if (this.getSetting().getValue() instanceof Float) {
                this.getSetting().setValue(Float.valueOf(finalValue));
            } else if (this.getSetting().getValue() instanceof Integer) {
                this.getSetting().setValue((int)finalValue);
            } else if (this.getSetting().getValue() instanceof Long) {
                this.getSetting().setValue((long)finalValue);
            } else if (this.getSetting().getValue() instanceof Short) {
                this.getSetting().setValue((short)finalValue);
            } else if (this.getSetting().getValue() instanceof Byte) {
                this.getSetting().setValue((byte)finalValue);
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.sliderHovered(mouseX, mouseY) && click.equals((Object)Click.LEFT)) {
            this.dragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }

    private boolean sliderHovered(float mouseX, float mouseY) {
        return mouseX >= this.getX() + 4.0f && mouseY >= this.getY() && mouseX <= this.getX() + this.getWidth() - 4.0f && mouseY <= this.getY() + this.getHeight();
    }
}

