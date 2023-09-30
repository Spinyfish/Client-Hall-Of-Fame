/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import java.awt.Color;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.SettingDrawable;

public class EnumDrawable
extends SettingDrawable<Enum<?>> {
    public EnumDrawable(Drawable parent, Setting<Enum<?>> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5.0f, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, this.getHover().getAnimationFactor() / 2.0 + (double)0.3f));
        Wrapper.getFont().drawString(this.getSetting().getName(), this.getX() + 5.0f, this.getY() + 2.0f, Color.WHITE, false);
        Wrapper.getFont().drawString(StringUtil.formatEnum((Enum)this.getSetting().getValue()), this.getX() + this.getWidth() - (float)Wrapper.getFont().getStringWidth(StringUtil.formatEnum((Enum)this.getSetting().getValue())) - 5.0f, this.getY() + 2.0f, Color.GRAY.brighter(), false);
        Wrapper.getFont().drawString(this.getSetting().getDescription(), (float)((double)(this.getX() + 5.0f) - (double)((float)Wrapper.getFont().getStringWidth(this.getSetting().getDescription()) - (this.getWidth() - 10.0f)) * this.getDescriptionHover().getAnimationFactor()), this.getY() + 15.0f, Color.GRAY, false);
        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.getHover().getState() && click.equals((Object)Click.LEFT)) {
            this.getSetting().setValue(this.getSetting().getMode(false));
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        super.mouseReleased(mouseX, mouseY, click);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}

