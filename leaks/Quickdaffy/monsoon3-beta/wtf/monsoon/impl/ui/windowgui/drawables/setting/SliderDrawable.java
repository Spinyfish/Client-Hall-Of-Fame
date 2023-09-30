/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.SettingDrawable;

public class SliderDrawable
extends SettingDrawable<Number> {
    private boolean dragging = false;
    private float renderWidth = 0.0f;
    private final Animation sliderAnimation = new Animation(() -> Float.valueOf(600.0f), false, () -> Easing.LINEAR);

    public SliderDrawable(Drawable parent, Setting<Number> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5.0f, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, this.getHover().getAnimationFactor() / 2.0 + (double)0.3f));
        Wrapper.getFont().drawString(this.getSetting().getName(), this.getX() + 5.0f, this.getY() + 2.0f, Color.WHITE, false);
        Wrapper.getFont().drawString(this.getSetting().getDescription(), (float)((double)(this.getX() + 5.0f) - (double)((float)Wrapper.getFont().getStringWidth(this.getSetting().getDescription()) - (this.getWidth() - 10.0f)) * this.getDescriptionHover().getAnimationFactor()), this.getY() + 15.0f, Color.GRAY, false);
        double diff = Math.min(this.getWidth(), Math.max(0.0f, mouseX - (this.getX() + 8.0f)));
        float localWidth = 0.0f;
        if (this.getSetting().getValue() instanceof Double) {
            double min = ((Number)this.getSetting().getMinimum()).doubleValue();
            double max = ((Number)this.getSetting().getMaximum()).doubleValue();
            localWidth = (float)((double)(this.getWidth() - 16.0f) * (((Number)this.getSetting().getValue()).doubleValue() - min) / (max - min));
            if (!Mouse.isButtonDown((int)0)) {
                this.dragging = false;
            }
            if (this.dragging) {
                if (diff == 0.0) {
                    this.getSetting().setValue(min);
                } else {
                    double newValue = MathUtils.round(diff / (double)(this.getWidth() - 16.0f) * (max - min) + min, 2.0);
                    double precision = 1.0 / ((Number)this.getSetting().getIncrementation()).doubleValue();
                    newValue = (double)Math.round(Math.max(min, Math.min(max, newValue)) * precision) / precision;
                    this.getSetting().setValue(newValue);
                }
            }
        } else if (this.getSetting().getValue() instanceof Float) {
            float min = ((Number)this.getSetting().getMinimum()).floatValue();
            float max = ((Number)this.getSetting().getMaximum()).floatValue();
            localWidth = (this.getWidth() - 16.0f) * (((Number)this.getSetting().getValue()).floatValue() - min) / (max - min);
            if (!Mouse.isButtonDown((int)0)) {
                this.dragging = false;
            }
            if (this.dragging) {
                if (diff == 0.0) {
                    this.getSetting().setValue(Float.valueOf(min));
                } else {
                    float newValue = (float)MathUtils.round(diff / (double)(this.getWidth() - 16.0f) * (double)(max - min) + (double)min, 2.0);
                    float precision = 1.0f / ((Number)this.getSetting().getIncrementation()).floatValue();
                    newValue = (float)Math.round(Math.max(min, Math.min(max, newValue)) * precision) / precision;
                    this.getSetting().setValue(Float.valueOf(newValue));
                }
            }
        }
        this.sliderAnimation.setState(this.renderWidth != localWidth);
        if (localWidth > this.renderWidth) {
            float widthDifference = this.renderWidth - localWidth;
            this.renderWidth -= widthDifference * (float)this.sliderAnimation.getAnimationFactor();
        }
        if (localWidth < this.renderWidth) {
            float widthDifference = localWidth - this.renderWidth;
            this.renderWidth += widthDifference * (float)this.sliderAnimation.getAnimationFactor();
        }
        RoundedUtils.round(this.getX() + 8.0f, this.getY() + 30.0f, this.getWidth() - 16.0f, 6.0f, 2.0f, WindowGUI.INTERACTABLE.darker());
        RoundedUtils.gradient(this.getX() + 8.0f, this.getY() + 30.0f, this.renderWidth, 6.0f, 2.0f, 1.0f, ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));
        RoundedUtils.round(this.getX() + 8.0f + this.renderWidth - 6.0f, this.getY() + 27.0f, 12.0f, 12.0f, 5.0f, ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));
        Wrapper.getFont().drawString(((Number)this.getSetting().getValue()).toString(), this.getX() + this.getWidth() - (float)Wrapper.getFont().getStringWidth(((Number)this.getSetting().getValue()).toString()) - 5.0f, this.getY() + 4.0f, Color.GRAY.brighter(), false);
        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.getHover().getState() && click.equals((Object)Click.LEFT)) {
            this.dragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        this.dragging = false;
        super.mouseReleased(mouseX, mouseY, click);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}

