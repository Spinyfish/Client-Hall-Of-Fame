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
import java.util.Arrays;
import java.util.stream.Collectors;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class ElementSettingEnum
extends ElementSetting<Enum<?>> {
    private final ArrayList<Button> buttons = new ArrayList();
    private final Animation expandAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final Animation rotate = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);

    public ElementSettingEnum(Setting<Enum<?>> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
        Enum<?> enumeration = set.getValue();
        String[] values = (String[])Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);
        for (int i = 0; i < values.length; ++i) {
            this.buttons.add(new Button(i, this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        super.draw(mouseX, mouseY, mouseDelta);
        this.rotate.setState(this.expandAnimation.getState());
        RenderUtil.rotate(this.getX() + this.getWidth() - 9.0f, this.getY() + this.getHeight() / 2.0f - 0.5f, 360.0f - (float)(this.rotate.getAnimationFactor() * 180.0), () -> Wrapper.getFontUtil().entypo14.drawCenteredString(FontUtil.UNICODES_UI.UP, this.getX() + this.getWidth() - 9.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFontUtil().entypo14.getHeight() / 2.0f - 1.5f, new Color(-7368817), false));
        if (this.expandAnimation.getAnimationFactor() > 0.0) {
            int i = 0;
            for (Button button : this.buttons) {
                button.setX(this.getX());
                button.setY(this.getY() + this.getHeight() + this.getHeight() * (float)i);
                button.draw(mouseX, mouseY, mouseDelta);
                ++i;
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY) && click.equals((Object)Click.LEFT)) {
            this.expandAnimation.setState(!this.expandAnimation.getState());
        }
        if (this.expandAnimation.getState()) {
            for (Button button : this.buttons) {
                button.mouseClicked(mouseX, mouseY, click);
            }
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public float getOffset() {
        return super.getOffset() + (float)this.buttons.size() * this.getHeight() * (float)this.expandAnimation.getAnimationFactor();
    }

    private class Button
    extends Drawable {
        private final int ordinal;
        private final Animation hover;

        public Button(int ordinal, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
            this.ordinal = ordinal;
        }

        @Override
        public void draw(float mouseX, float mouseY, int mouseDelta) {
            this.hover.setState(this.ordinal == ((Enum)ElementSettingEnum.this.getSetting().getValue()).ordinal());
            Enum enumeration = (Enum)ElementSettingEnum.this.getSetting().getValue();
            RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
            RenderUtil.rect(this.getX() + 1.5f, this.getY(), this.getWidth() - 3.0f, this.getHeight(), ColorUtil.interpolate(new Color(0x2A2A2A), new Color(0x333333), this.hover.getAnimationFactor()));
            Wrapper.getFontUtil().productSansSmall.drawString(StringUtil.formatEnum((Enum)Arrays.stream(enumeration.getClass().getEnumConstants()).filter(e -> e.ordinal() == this.ordinal).collect(Collectors.toList()).get(0)), this.getX() + 4.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFont().getHeight() / 2.0f, new Color(-7368817), false);
        }

        @Override
        public boolean mouseClicked(float mouseX, float mouseY, Click click) {
            if (this.hovered(mouseX, mouseY)) {
                Enum enumeration = (Enum)ElementSettingEnum.this.getSetting().getValue();
                ElementSettingEnum.this.getSetting().setValue(Enum.valueOf(enumeration.getClass(), ((String[])Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new))[this.ordinal]));
            }
            return false;
        }

        @Override
        public void mouseReleased(float mouseX, float mouseY, Click click) {
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {
        }
    }
}

