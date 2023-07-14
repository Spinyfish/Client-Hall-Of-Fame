/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.windowgui.drawables;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.CategoryDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.SettingDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BindDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BooleanDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.EnumDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.SliderDrawable;

public class ModuleDrawable
extends Drawable {
    private final CategoryDrawable parent;
    private final Module module;
    private CategoryDrawable.Column column;
    private final List<SettingDrawable<?>> settings = new ArrayList();
    private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final Animation descriptionHover = new Animation(() -> Float.valueOf(2000.0f), false, () -> Easing.LINEAR);
    private final Animation enabled = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final Animation expand = new Animation(() -> Float.valueOf(300.0f), false, () -> Easing.CUBIC_IN_OUT);

    public ModuleDrawable(CategoryDrawable parent, Module module, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        this.module = module;
        this.module.getSettings().forEach(setting -> {
            if (setting.getValue() instanceof Boolean) {
                this.settings.add(new BooleanDrawable((Drawable)this, (Setting<Boolean>)setting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight()));
            } else if (setting.getValue() instanceof Enum) {
                this.settings.add(new EnumDrawable((Drawable)this, (Setting<Enum<?>>)setting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight()));
            } else if (setting.getValue() instanceof Number) {
                this.settings.add(new SliderDrawable((Drawable)this, (Setting<Number>)setting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight() + 10.0f));
            } else if (setting.getValue() instanceof Bind) {
                this.settings.add(new BindDrawable((Drawable)this, (Setting<Bind>)setting, this.getX() + 2.0f, this.getY(), this.getWidth() - 4.0f, this.getHeight()));
            }
        });
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        this.hover.setState(MathUtils.within(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY));
        this.descriptionHover.setState(MathUtils.within(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) && (float)Wrapper.getFont().getStringWidth(this.getModule().getDescription()) >= this.getWidth() - 5.0f);
        this.enabled.setState(this.module.isEnabled());
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight() + this.getOffset(), 6.0f, ColorUtil.interpolate(WindowGUI.INTERACTABLE, WindowGUI.INTERACTABLE.brighter(), 0.6));
        RoundedUtils.gradient(this.getX(), this.getY(), this.getWidth(), this.getHeight() + this.getOffset(), 6.0f, (float)this.enabled.getAnimationFactor(), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 45, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 135, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));
        RoundedUtils.round(this.getX() + 1.0f, this.getY() + 1.0f, this.getWidth() - 2.0f, this.getHeight() - 2.0f, 6.0f, ColorUtil.interpolate(WindowGUI.INTERACTABLE, WindowGUI.HOVER, this.hover.getAnimationFactor()));
        Wrapper.getFont().drawString(this.getModule().getName(), this.getX() + 5.0f, this.getY() + 2.0f, Color.WHITE, false);
        RenderUtil.pushScissor(this.getX() + 2.0f, MathHelper.clamp_float(this.getY() + 15.0f, this.getParent().getParent().getY() + 25.0f, 100000.0f), this.getWidth() - 4.0f, this.getParent().getParent().getY() + 25.0f + (this.getParent().getParent().getHeight() - 82.0f) - (this.getY() + 13.0f));
        Wrapper.getFont().drawString(this.getModule().getDescription(), (float)((double)(this.getX() + 5.0f) - (double)((float)Wrapper.getFont().getStringWidth(this.getModule().getDescription()) - (this.getWidth() - 10.0f)) * this.descriptionHover.getAnimationFactor()), this.getY() + 15.0f, Color.GRAY.brighter(), false);
        RenderUtil.popScissor();
        if (this.expand.getAnimationFactor() > 0.0) {
            float subY = this.getY() + 30.0f;
            RenderUtil.pushScissor(this.getX() + 2.0f, MathHelper.clamp_float(this.getY() + 30.0f, this.getParent().getParent().getY() + 26.0f, 100000.0f), this.getWidth() - 4.0f, MathHelper.clamp_double(this.getOffset(), 0.0, (double)(this.getParent().getParent().getY() + 25.0f + (this.getParent().getParent().getHeight() - 80.0f) - (this.getY() + 30.0f)) * this.expand.getAnimationFactor()));
            for (SettingDrawable<?> settingDrawable : this.settings) {
                if (!settingDrawable.getSetting().isVisible()) continue;
                settingDrawable.setX(this.getX() + 2.0f);
                settingDrawable.setY(subY);
                settingDrawable.draw(mouseX, mouseY, mouseDelta);
                subY += settingDrawable.getHeight() + settingDrawable.getOffset();
            }
            RenderUtil.popScissor();
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hover.getState()) {
            if (click.equals((Object)Click.LEFT)) {
                this.module.toggle();
            } else if (click.equals((Object)Click.RIGHT)) {
                this.expand.setState(!this.expand.getState());
            }
        }
        if (this.expand.getState()) {
            this.settings.forEach(settingDrawable -> {
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
            this.settings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.mouseReleased(mouseX, mouseY, click);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.expand.getState()) {
            this.settings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    @Override
    public float getOffset() {
        float settingHeight = 2.0f;
        for (SettingDrawable<?> setting : this.settings) {
            if (!setting.getSetting().isVisible()) continue;
            settingHeight += setting.getHeight() + setting.getOffset();
        }
        return (float)((double)settingHeight * this.expand.getAnimationFactor());
    }

    public CategoryDrawable getParent() {
        return this.parent;
    }

    public Module getModule() {
        return this.module;
    }

    public CategoryDrawable.Column getColumn() {
        return this.column;
    }

    public void setColumn(CategoryDrawable.Column column) {
        this.column = column;
    }

    public List<SettingDrawable<?>> getSettings() {
        return this.settings;
    }
}

