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
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.primitive.Click;

public class ElementSettingBoolean
extends ElementSetting<Boolean> {
    private final Animation toggle = new Animation(() -> Float.valueOf(150.0f), ((Boolean)this.getSetting().getValue()).booleanValue(), () -> Easing.LINEAR);

    public ElementSettingBoolean(Setting<Boolean> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        super.draw(mouseX, mouseY, mouseDelta);
        this.toggle.setState(((Boolean)this.getSetting().getValue()).booleanValue());
        Color toggleColor = ColorUtil.interpolate(new Color(0x1E1E1E), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), this.toggle.getAnimationFactor());
        RoundedUtils.round(this.getX() + this.getWidth() - 14.0f, this.getY() + 2.0f, 10.0f, 10.0f, 2.0f, new Color(0x1E1E1E));
        if (this.toggle.getAnimationFactor() > 0.0) {
            Wrapper.getFontUtil().entypo14.drawCenteredString(FontUtil.UNICODES_UI.YES, this.getX() + this.getWidth() - 9.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFontUtil().entypo14.getHeight() / 2.0f - 1.5f, ColorUtil.integrateAlpha(toggleColor, (float)(this.toggle.getAnimationFactor() * 255.0)), false);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY) && click.equals((Object)Click.LEFT)) {
            this.getSetting().setValue((Boolean)this.getSetting().getValue() == false);
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }
}

