/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.panel.elements.setting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingBoolean;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingColor;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingEnum;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingKey;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSettingSlider;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class ElementSetting<T>
extends Drawable {
    private final Setting<T> setting;
    private final Animation expandAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final List<ElementSetting<?>> subsettings = new ArrayList();

    public ElementSetting(Setting<T> setting, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.setting = setting;
        for (Setting<Enum<?>> setting2 : setting.getChildren()) {
            if (setting2.getValue() instanceof Enum) {
                this.subsettings.add(new ElementSettingEnum(setting2, this.getX(), this.getY(), this.getWidth() - 2.0f, this.getHeight()));
                continue;
            }
            if (setting2.getValue() instanceof Boolean) {
                this.subsettings.add(new ElementSettingBoolean((Setting<Boolean>)setting2, this.getX(), this.getY(), this.getWidth() - 2.0f, this.getHeight()));
                continue;
            }
            if (setting2.getValue() instanceof Number) {
                this.subsettings.add(new ElementSettingSlider((Setting<Number>)setting2, this.getX(), this.getY(), this.getWidth() - 2.0f, this.getHeight()));
                continue;
            }
            if (setting2.getValue() instanceof Bind) {
                this.subsettings.add(new ElementSettingKey((Setting<Bind>)setting2, this.getX(), this.getY(), this.getWidth() - 2.0f, this.getHeight()));
                continue;
            }
            if (setting2.getValue() instanceof Color) {
                this.subsettings.add(new ElementSettingColor((Setting<Color>)setting2, this.getX(), this.getY(), this.getWidth() - 2.0f, this.getHeight() * 4.0f));
                continue;
            }
            if (setting2.getValue() == null) continue;
            this.subsettings.add(new ElementSetting(setting2, this.getX(), this.getY(), this.getWidth() - 2.0f, this.getHeight()));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        this.defaultRender(mouseX, mouseY, mouseDelta);
    }

    protected void defaultRender(float mouseX, float mouseY, int mouseDelta) {
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(this.getX() + 1.0f, this.getY(), this.getWidth() - 2.0f, this.getHeight(), new Color(0x252525));
        float offset = 0.0f;
        if (this.subsettings.stream().anyMatch(subsetting -> subsetting.getSetting().isVisible())) {
            offset = 8.0f;
            RenderUtil.rotate(this.getX() + 6.5f, this.getY() + 8.5f, -90.0 * this.expandAnimation.getAnimationFactor(), () -> Wrapper.getFontUtil().entypo14.drawString(FontUtil.UNICODES_UI.RIGHT, this.getX() + 5.5f, this.getY() + 5.0f, new Color(255, 255, 255, 150), false));
        }
        Wrapper.getFontUtil().productSansSmall.drawString(this.setting.getName(), this.getX() + 4.0f + offset, this.getY() + 4.0f, new Color(-7368817), false);
        if (this.expandAnimation.getAnimationFactor() > 0.0 && this.subsettings.stream().anyMatch(subsetting -> subsetting.getSetting().isVisible())) {
            RenderUtil.rect(this.getX(), this.getY() + this.getHeight(), this.getWidth(), this.getOffset(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
            float settingOffset = this.getY() + this.getHeight();
            for (ElementSetting<?> settingElement : this.subsettings) {
                if (!settingElement.getSetting().isVisible()) continue;
                settingElement.setX(this.getX() + 1.0f);
                settingElement.setY(settingOffset);
                settingElement.draw(mouseX, mouseY, mouseDelta);
                settingOffset += settingElement.getOffset();
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY) && click.equals((Object)Click.RIGHT)) {
            this.expandAnimation.setState(!this.expandAnimation.getState());
        }
        if (this.expandAnimation.getState()) {
            this.subsettings.forEach(subsetting -> {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.mouseClicked(mouseX, mouseY, click);
                }
            });
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        if (this.expandAnimation.getState()) {
            this.subsettings.forEach(subsetting -> {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.mouseReleased(mouseX, mouseY, click);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.expandAnimation.getState()) {
            this.subsettings.forEach(subsetting -> {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    private float getSubsettingHeight() {
        float subsettingHeight = 0.0f;
        for (ElementSetting<?> subsetting : this.subsettings) {
            if (!subsetting.getSetting().isVisible()) continue;
            subsettingHeight += subsetting.getOffset();
        }
        return (float)((double)subsettingHeight * this.expandAnimation.getAnimationFactor());
    }

    @Override
    public float getOffset() {
        return this.getHeight() + this.getSubsettingHeight();
    }

    public Setting<T> getSetting() {
        return this.setting;
    }
}

