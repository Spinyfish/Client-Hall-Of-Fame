/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.primitive;

import java.awt.Color;
import lombok.NonNull;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.primitive.ButtonHandler;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class Button
extends Drawable {
    Animation animation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.SINE_IN_OUT);
    FontRenderer fontRenderer;
    String text;
    float padding;
    ButtonHandler handle;

    public Button(float x, float y, FontRenderer fontRenderer, String text, float padding, ButtonHandler handle) {
        super(x, y, 0.0f, 0.0f);
        this.fontRenderer = fontRenderer;
        this.text = text;
        this.padding = padding;
        this.handle = handle;
    }

    @Override
    @NonNull
    public float getWidth() {
        return (float)this.fontRenderer.getStringWidth(this.text) + this.padding;
    }

    @Override
    @NonNull
    public float getHeight() {
        return (float)this.fontRenderer.getHeight() + this.padding;
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        this.animation.setState(mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight());
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), new Color(0x131313));
        this.fontRenderer.drawCenteredString(this.text, this.getX() + this.getWidth() / 2.0f - 1.0f, this.getY() + this.getHeight() / 2.0f - (float)this.fontRenderer.getHeight() / 2.0f - 1.0f, Color.WHITE, false);
        RenderUtil.rect(this.getX() + this.getWidth() / 2.0f - (float)this.animation.getAnimationFactor() * (this.getWidth() / 2.0f - 2.0f), this.getY() + this.getHeight() - 2.0f, (float)this.animation.getAnimationFactor() * (this.getWidth() - 4.0f), 1.0f, ColorUtil.getClientAccentTheme()[0]);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight()) {
            this.handle.onClick(click);
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

