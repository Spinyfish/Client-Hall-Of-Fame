/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.panel.elements.setting;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.primitive.Click;

public class ElementSettingKey
extends ElementSetting<Bind> {
    private final Animation listening = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);

    public ElementSettingKey(Setting<Bind> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        String bindName = this.listening.getState() ? "Listening" : ((Bind)this.getSetting().getValue()).getButtonName();
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(this.getX() + 1.0f, this.getY(), this.getWidth() - 2.0f, this.getHeight(), new Color(0x252525));
        Wrapper.getFontUtil().productSansSmall.drawString(this.getSetting().getName(), this.getX() + 4.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFont().getHeight() / 2.0f, new Color(-7368817), false);
        Wrapper.getFontUtil().productSansSmall.drawString("[" + bindName + "]", this.getX() + this.getWidth() - (float)Wrapper.getFontUtil().productSansSmall.getStringWidth(bindName) - 12.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFont().getHeight() / 2.0f, ColorUtil.interpolate(new Color(-7368817), new Color(0x525252), this.listening.getAnimationFactor()), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY) && click.equals((Object)Click.LEFT)) {
            this.listening.setState(!this.listening.getState());
            return false;
        }
        if (this.listening.getState()) {
            this.listening.setState(false);
            this.getSetting().setValue(new Bind(click.getButton(), Bind.Device.MOUSE));
            return false;
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.listening.getState()) {
            this.listening.setState(false);
            if (keyCode < 1) {
                return;
            }
            if (keyCode == 211 || keyCode == 14) {
                this.getSetting().setValue(new Bind(0, Bind.Device.KEYBOARD));
                return;
            }
            this.getSetting().setValue(new Bind(keyCode, Bind.Device.KEYBOARD));
        }
        super.keyTyped(typedChar, keyCode);
    }
}

