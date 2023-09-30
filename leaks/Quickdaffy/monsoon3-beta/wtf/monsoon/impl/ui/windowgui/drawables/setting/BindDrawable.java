/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.SettingDrawable;

public class BindDrawable
extends SettingDrawable<Bind> {
    private final Animation listening = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);

    public BindDrawable(Drawable parent, Setting<Bind> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.gradient(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 6.0f, (float)this.listening.getAnimationFactor(), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 45, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 135, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 6.0f, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, this.getHover().getAnimationFactor() / 2.0 + (double)0.3f));
        Wrapper.getFont().drawString(this.getSetting().getName(), this.getX() + 5.0f, this.getY() + 2.0f, Color.WHITE, false);
        Wrapper.getFont().drawString(this.listening.getState() ? "..." : ((Bind)this.getSetting().getValue()).getButtonName(), this.getX() + this.getWidth() - (float)Wrapper.getFont().getStringWidth(this.listening.getState() ? "..." : ((Bind)this.getSetting().getValue()).getButtonName()) - 5.0f, this.getY() + 2.0f, Color.GRAY.brighter(), false);
        Wrapper.getFont().drawString(this.getSetting().getDescription(), (float)((double)(this.getX() + 5.0f) - (double)((float)Wrapper.getFont().getStringWidth(this.getSetting().getDescription()) - (this.getWidth() - 10.0f)) * this.getDescriptionHover().getAnimationFactor()), this.getY() + 15.0f, Color.GRAY, false);
        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.getHover().getState() && click.equals((Object)Click.LEFT)) {
            this.listening.setState(!this.listening.getState());
            return true;
        }
        if (this.listening.getState()) {
            this.listening.setState(false);
            this.getSetting().setValue(new Bind(click.getButton(), Bind.Device.MOUSE));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        super.mouseReleased(mouseX, mouseY, click);
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

