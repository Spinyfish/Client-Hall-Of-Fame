/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.login;

import java.awt.Color;
import java.io.IOException;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.Gui;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.login.draw.LoginButton;
import wtf.monsoon.impl.ui.login.draw.LoginEmailField;
import wtf.monsoon.impl.ui.login.draw.LoginPassField;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.misc.protection.ProtectedInitialization;

public class LoginScreen
extends ScalableScreen {
    Animation anim1 = new Animation(() -> Float.valueOf(800.0f), false, () -> Easing.LINEAR);
    Animation anim2 = new Animation(() -> Float.valueOf(400.0f), false, () -> Easing.LINEAR);
    Animation anim3 = new Animation(() -> Float.valueOf(500.0f), false, () -> Easing.CIRC_OUT);
    int progress = 0;
    LoginEmailField emailField;
    LoginPassField passField;
    LoginButton loginButton;
    private Timer loginCheckTimer = new Timer();
    private boolean checkingLogin = false;
    private boolean attemptingLogin = false;

    @Override
    public void init() {
        this.emailField = new LoginEmailField(0.0f, 0.0f, 120.0f, 20.0f);
        this.passField = new LoginPassField(0.0f, 0.0f, 120.0f, 20.0f);
        this.loginButton = new LoginButton(0.0f, 0.0f, 120.0f, 20.0f, this);
    }

    @Override
    public void render(float mouseX, float mouseY) {
        if (Wrapper.loggedIn) {
            this.mc.displayGuiScreen(new MainMenu());
        }
        if (this.progress == 0) {
            this.anim1.setState(true);
        }
        if (this.anim1.getAnimationFactor() == 1.0) {
            this.anim2.setState(true);
        }
        if (this.anim2.getAnimationFactor() == 1.0) {
            ++this.progress;
        }
        if (this.progress == 1) {
            this.anim3.setState(true);
        }
        Color accent1 = new Color(0, 238, 255, 255);
        Color accent2 = new Color(135, 56, 232, 255);
        Color background = Color.BLACK;
        Gui.drawRect(0.0, 0.0, this.scaledWidth, this.scaledHeight, background.getRGB());
        this.emailField.setX(this.scaledWidth / 2.0f - this.emailField.getWidth() / 2.0f);
        this.emailField.setY(this.scaledHeight / 2.0f - this.emailField.getHeight() / 2.0f);
        this.passField.setX(this.scaledWidth / 2.0f - this.passField.getWidth() / 2.0f);
        this.passField.setY(this.scaledHeight / 2.0f - this.passField.getHeight() / 2.0f + this.passField.getHeight() + 12.0f);
        this.loginButton.setEmail(this.emailField.getEmail());
        this.loginButton.setPassword(this.passField.getPassword());
        this.loginButton.setX(this.scaledWidth / 2.0f - this.loginButton.getWidth() / 2.0f);
        this.loginButton.setY(this.scaledHeight / 2.0f - this.loginButton.getHeight() / 2.0f + this.loginButton.getHeight() * 2.0f + 24.0f);
        RenderUtil.scaleX(this.scaledWidth / 2.0f, this.scaledHeight / 2.0f, this.anim3, () -> {
            Wrapper.getFontUtil().productSansSmall.drawCenteredString("email", this.getScaledWidth() / 2.0f, this.emailField.getY() - 10.0f, new Color(0x3A3A3A), false);
            this.emailField.draw(mouseX, mouseY, 0);
            Wrapper.getFontUtil().productSansSmall.drawCenteredString("password", this.getScaledWidth() / 2.0f, this.passField.getY() - 10.0f, new Color(0x3A3A3A), false);
            this.passField.draw(mouseX, mouseY, 0);
            this.loginButton.draw(mouseX, mouseY, 0);
        });
        Wrapper.getFontUtil().greycliff40.drawCenteredString("Hi.", this.scaledWidth / 2.0f, (float)((double)(this.scaledHeight / 2.0f - (float)Wrapper.getFontUtil().greycliff40.getHeight() / 2.0f) - 70.0 * this.anim3.getAnimationFactor()), ColorUtil.interpolate(background, Color.GRAY, this.anim1.getAnimationFactor()), false);
        Wrapper.getFontUtil().greycliff19.drawCenteredString("Before we continue you need to login.", this.scaledWidth / 2.0f, (float)((double)(this.scaledHeight / 2.0f - (float)Wrapper.getFontUtil().greycliff19.getHeight() / 2.0f + 18.0f) - 70.0 * this.anim3.getAnimationFactor()), ColorUtil.interpolate(background, Color.GRAY, this.anim2.getAnimationFactor()), false);
        this.checkLogin();
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        this.emailField.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        this.passField.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        this.attemptingLogin = this.loginButton.mouseClickedLoginutton(mouseX, mouseY, Click.getClick(mouseButton));
    }

    public void checkLogin() {
        if (this.attemptingLogin) {
            this.checkingLogin = true;
            this.loginCheckTimer.reset();
            if (this.loginCheckTimer.hasTimeElapsed(3000L, true) && Wrapper.getMonsoon().getNetworkManager().getProtectedInitialization() != null) {
                ProtectedInitialization protectedInitialization = Wrapper.getMonsoon().getNetworkManager().getProtectedInitialization();
                protectedInitialization.checkIfDoneLoading();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.emailField.keyTyped(typedChar, keyCode);
        this.passField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}

