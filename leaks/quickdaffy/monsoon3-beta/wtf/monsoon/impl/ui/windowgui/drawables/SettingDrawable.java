/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.windowgui.drawables;

import java.util.ArrayList;
import java.util.List;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BindDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BooleanDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.EnumDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.SliderDrawable;

public class SettingDrawable<T>
extends Drawable {
    private final Drawable parent;
    private final Setting<T> setting;
    private final List<SettingDrawable<?>> subsettings = new ArrayList();
    private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final Animation descriptionHover = new Animation(() -> Float.valueOf(2000.0f), false, () -> Easing.LINEAR);
    private final Animation expand = new Animation(() -> Float.valueOf(400.0f), false, () -> Easing.CUBIC_IN_OUT);

    public SettingDrawable(Drawable parent, Setting<T> setting, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        this.setting = setting;
        this.setting.getChildren().forEach(subsetting -> {
            if (subsetting.getValue() instanceof Boolean) {
                this.subsettings.add(new BooleanDrawable((Drawable)this, (Setting<Boolean>)subsetting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight()));
            } else if (subsetting.getValue() instanceof Enum) {
                this.subsettings.add(new EnumDrawable((Drawable)this, (Setting<Enum<?>>)subsetting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight()));
            } else if (subsetting.getValue() instanceof Number) {
                this.subsettings.add(new SliderDrawable((Drawable)this, (Setting<Number>)subsetting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight() + 10.0f));
            } else if (subsetting.getValue() instanceof Bind) {
                this.subsettings.add(new BindDrawable((Drawable)this, (Setting<Bind>)subsetting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight()));
            }
        });
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        this.hover.setState(MathUtils.within(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY));
        this.descriptionHover.setState(MathUtils.within(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) && (float)Wrapper.getFont().getStringWidth(this.getSetting().getDescription()) >= this.getWidth() - 5.0f);
        if (this.expand.getAnimationFactor() > 0.0) {
            float subY = this.getY() + 30.0f;
            for (SettingDrawable<?> subsetting : this.subsettings) {
                if (!subsetting.getSetting().isVisible()) continue;
                subsetting.setX(this.getX() + 2.0f);
                subsetting.setY(subY);
                subsetting.draw(mouseX, mouseY, mouseDelta);
                subY += subsetting.getHeight() + subsetting.getOffset();
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hover.getState() && click.equals((Object)Click.RIGHT)) {
            this.expand.setState(!this.expand.getState());
        }
        if (this.expand.getState()) {
            this.subsettings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.mouseClicked(mouseX, mouseY, click);
                }
            });
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        if (this.expand.getState()) {
            this.subsettings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.mouseReleased(mouseX, mouseY, click);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.expand.getState()) {
            this.subsettings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    @Override
    public float getOffset() {
        float subsettingHeight = 0.0f;
        for (SettingDrawable<?> subsetting : this.subsettings) {
            if (!subsetting.getSetting().isVisible()) continue;
            subsettingHeight += subsetting.getHeight();
        }
        return (float)((double)subsettingHeight * this.expand.getAnimationFactor());
    }

    public Drawable getParent() {
        return this.parent;
    }

    public Setting<T> getSetting() {
        return this.setting;
    }

    public List<SettingDrawable<?>> getSubsettings() {
        return this.subsettings;
    }

    public Animation getHover() {
        return this.hover;
    }

    public Animation getDescriptionHover() {
        return this.descriptionHover;
    }

    public Animation getExpand() {
        return this.expand;
    }
}

