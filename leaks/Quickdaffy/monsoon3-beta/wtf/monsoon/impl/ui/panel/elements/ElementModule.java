/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.panel.elements;

import java.awt.Color;
import java.util.ArrayList;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingBoolean;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingColor;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingEnum;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingKey;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingSlider;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class ElementModule
extends Drawable {
    private final Module module;
    private final Animation expandAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final ArrayList<ElementSetting<?>> settings = new ArrayList();

    public ElementModule(Module module, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.module = module;
        for (Setting<Enum<?>> setting : module.getSettings()) {
            if (setting.getValue() instanceof Enum) {
                this.settings.add(new ElementSettingEnum(setting, this.getX(), this.getY(), this.getWidth(), this.getHeight()));
                continue;
            }
            if (setting.getValue() instanceof Boolean) {
                this.settings.add(new ElementSettingBoolean((Setting<Boolean>)setting, this.getX(), this.getY(), this.getWidth(), this.getHeight()));
                continue;
            }
            if (setting.getValue() instanceof Number) {
                this.settings.add(new ElementSettingSlider((Setting<Number>)setting, this.getX(), this.getY(), this.getWidth(), this.getHeight()));
                continue;
            }
            if (setting.getValue() instanceof Bind) {
                this.settings.add(new ElementSettingKey((Setting<Bind>)setting, this.getX(), this.getY(), this.getWidth(), this.getHeight()));
                continue;
            }
            if (setting.getValue() instanceof Color) {
                this.settings.add(new ElementSettingColor((Setting<Color>)setting, this.getX(), this.getY(), this.getWidth(), this.getHeight() * 4.0f));
                continue;
            }
            if (setting.getValue() == null) continue;
            this.settings.add(new ElementSetting(setting, this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        if (this.expandAnimation.getAnimationFactor() > 0.0) {
            float settingOffset = this.getY() + this.getHeight();
            for (ElementSetting<?> settingElement : this.settings) {
                if (!settingElement.getSetting().isVisible()) continue;
                settingElement.setX(this.getX());
                settingElement.setY(settingOffset);
                settingElement.draw(mouseX, mouseY, mouseDelta);
                settingOffset += settingElement.getOffset();
            }
        }
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), new Color(0x1F1F1F));
        Wrapper.getFontUtil().productSansSmall.drawString(this.module.getName(), this.getX() + 4.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFont().getHeight() / 2.0f, this.module.isEnabled() ? new Color(-7368817) : new Color(-10790053), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY)) {
            switch (click) {
                case LEFT: {
                    this.module.toggle();
                    break;
                }
                case RIGHT: {
                    this.expandAnimation.setState(!this.expandAnimation.getState());
                }
            }
        }
        if (this.expandAnimation.getState()) {
            for (ElementSetting<?> setting : this.settings) {
                if (!setting.getSetting().isVisible()) continue;
                setting.mouseClicked(mouseX, mouseY, click);
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (ElementSetting<?> setting : this.settings) {
            setting.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public float getOffset() {
        float settingHeight = 0.0f;
        for (ElementSetting<?> settingElement : this.settings) {
            if (!settingElement.getSetting().isVisible()) continue;
            settingHeight += settingElement.getOffset();
        }
        return (float)((double)this.getHeight() + (double)settingHeight * this.expandAnimation.getAnimationFactor());
    }
}

