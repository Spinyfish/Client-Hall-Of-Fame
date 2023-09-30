/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.ui.notification;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.module.hud.NotificationsModule;
import wtf.monsoon.impl.ui.notification.NotificationType;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class Notification
extends Drawable {
    private final NotificationType type;
    private final String title;
    private final String description;
    private final Animation animation = new Animation(() -> Float.valueOf(700.0f), true, () -> Easing.BACK_IN_OUT);
    private long initTime = 0L;

    public Notification(float x, float y, NotificationType type, String title, String description) {
        super(x, y, 125.0f, 30.0f);
        this.type = type;
        this.title = title;
        this.description = description;
        this.animation.resetToDefault();
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        long time;
        if (this.initTime == 0L && this.animation.getAnimationFactor() >= 1.0) {
            this.initTime = System.currentTimeMillis();
        }
        long l = time = this.initTime > 0L ? System.currentTimeMillis() - this.initTime : 0L;
        if (time >= 1500L) {
            this.animation.setState(false);
        }
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5.0f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
        RenderUtil.pushScissor(this.getX(), this.getY(), MathHelper.clamp_float(this.getWidth() * ((float)time / 1500.0f), 0.0f, this.getWidth()), this.getHeight());
        RoundedUtils.gradient(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, Wrapper.getModule(NotificationsModule.class).barOpacity.getValue().floatValue(), ColorUtil.getAccent(Wrapper.getModule(NotificationsModule.class).barDarken.getValue().floatValue()));
        RenderUtil.popScissor();
        RoundedUtils.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5.0f, 2.0f, 2.0f, ColorUtil.getAccent());
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        float factor = 0.5f;
        Wrapper.getFontUtil().entypo18.drawString(this.type.icon, (this.getX() + 8.0f) * factor, (this.getY() + 6.0f) * factor, this.type.color, false);
        GL11.glScalef((float)factor, (float)factor, (float)factor);
        Wrapper.getFontUtil().productSans.drawString(this.title, this.getX() + 27.0f, this.getY() + 3.0f, Color.WHITE, false);
        Wrapper.getFontUtil().productSansSmall.drawString(this.description, this.getX() + 27.0f, this.getY() + 15.0f, Color.WHITE, false);
    }

    @Override
    public float getWidth() {
        float titleWidth = Wrapper.getFontUtil().productSans.getStringWidth(this.title);
        float descWidth = Wrapper.getFontUtil().productSansSmall.getStringWidth(this.description);
        return Math.max(titleWidth, descWidth) + 47.0f;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public boolean shouldNotificationHide() {
        return !this.animation.getState() && this.animation.getAnimationFactor() == 0.0;
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }
}

