/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.input.Keyboard
 */
package wtf.monsoon.impl.ui.login.draw;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class LoginEmailField
extends Drawable {
    String email = "";
    Animation focused = new Animation(() -> Float.valueOf(400.0f), false, () -> Easing.CIRC_IN_OUT);

    public LoginEmailField(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        Keyboard.enableRepeatEvents((boolean)true);
        Color accent1 = new Color(0, 238, 255, 255);
        Color accent2 = new Color(135, 56, 232, 255);
        Color background = Color.BLACK;
        Color fade1 = ColorUtil.fadeBetween(10, 0, accent1, accent2);
        float animVal = (float)this.focused.getAnimationFactor();
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 5.0f, new Color(0x111111));
        RoundedUtils.round(this.getX() + 1.0f, this.getY() + 1.0f, this.getWidth() - 2.0f, this.getHeight() - 2.0f, 4.0f, background);
        RenderUtil.rect(this.getX() + this.getWidth() / 2.0f - 4.0f - Math.max(20.0f, (float)Wrapper.getFontUtil().productSans.getStringWidth(this.email) / 2.0f - 10.0f) * animVal, this.getY() + this.getHeight() - 5.0f, 8.0f + Math.max(20.0f, (float)Wrapper.getFontUtil().productSans.getStringWidth(this.email) / 2.0f - 10.0f) * 2.0f * animVal, 1.0f, ColorUtil.interpolate(background, fade1, this.focused.getAnimationFactor() / 2.0));
        Wrapper.getFontUtil().productSans.drawCenteredString(this.email, this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFontUtil().productSans.getHeight() / 2.0f - 1.0f - animVal, ColorUtil.interpolate(new Color(0x3A3A3A), new Color(0x9D9D9D), this.focused.getAnimationFactor()), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY) && click.equals((Object)Click.LEFT)) {
            this.focused.setState(true);
        } else {
            this.focused.setState(false);
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.focused.getState()) {
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                this.email = this.email + typedChar;
            }
            if (this.email.length() != 0 && keyCode == 14) {
                this.email = this.email.substring(0, this.email.length() - 1);
            }
            if (GuiScreen.isCtrlKeyDown() && keyCode == 47) {
                try {
                    this.email = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getEmail() {
        return this.email;
    }
}

