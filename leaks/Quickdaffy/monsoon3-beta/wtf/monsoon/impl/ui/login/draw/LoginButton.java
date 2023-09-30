/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.login.draw;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.login.LoginScreen;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.misc.protection.ProtectedInitialization;

public class LoginButton
extends Drawable {
    Animation hovered = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    Animation enabled = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    String email = "";
    String password = "";
    private LoginScreen parent;

    public LoginButton(float x, float y, float width, float height, LoginScreen parent) {
        super(x, y, width, height);
        this.parent = parent;
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        this.enabled.setState(true);
        this.hovered.setState(this.hovered(mouseX, mouseY));
        Color accent1 = new Color(0, 238, 255, 255);
        Color accent2 = new Color(135, 56, 232, 255);
        Color background = Color.BLACK;
        Color fade1 = ColorUtil.fadeBetween(10, 0, accent1, accent2);
        float animVal = (float)this.hovered.getAnimationFactor();
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5.0f, new Color(0x111111));
        RoundedUtils.round(this.getX() + 1.0f, this.getY() + 1.0f, this.getWidth() - 2.0f, this.getHeight() - 2.0f, 4.0f, background);
        RenderUtil.rect(this.getX() + this.getWidth() / 2.0f - 4.0f - 30.0f * animVal, this.getY() + this.getHeight() - 5.0f, 8.0f + 60.0f * animVal, 1.0f, ColorUtil.interpolate(background, fade1, this.hovered.getAnimationFactor() / 2.0));
        Wrapper.getFontUtil().productSans.drawCenteredString("Login", this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFontUtil().productSans.getHeight() / 2.0f - animVal * 2.0f, ColorUtil.interpolate(new Color(0x3A3A3A), new Color(0x9D9D9D), this.enabled.getAnimationFactor()), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        return false;
    }

    public boolean mouseClickedLoginutton(float mouseX, float mouseY, Click click) {
        if (this.hovered.getState() && this.enabled.getState() && click.equals((Object)Click.LEFT)) {
            Wrapper.getMonsoon().getNetworkManager().email = this.email;
            Wrapper.getMonsoon().getNetworkManager().password = this.password;
            this.fakeLogin();
            return true;
        }
        return false;
    }

    private void fakeLogin() {
        Wrapper.loggedIn = true;
        Wrapper.getMonsoon().getNetworkManager().setConnected(true);
        Wrapper.getMonsoon().getNetworkManager().setProtectedInitialization(new ProtectedInitialization());
        Wrapper.getMonsoon().getNetworkManager().getProtectedInitialization().start();
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginScreen getParent() {
        return this.parent;
    }
}

