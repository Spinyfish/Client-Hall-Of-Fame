/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.ColourAnimation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.menu.windows;

import java.awt.Color;
import me.surge.animation.ColourAnimation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.BlurUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;

public class Window {
    private float x;
    private float y;
    private final float width;
    private float height;
    private final float header;
    private boolean dragging = false;
    boolean shouldClose;
    ColourAnimation closeButtonHover = new ColourAnimation(ColorUtil.TRANSPARENT, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.5), () -> Float.valueOf(250.0f), false, () -> Easing.LINEAR);
    private float lastX;
    private float lastY;

    public Window(float x, float y, float width, float height, float header) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.header = header;
    }

    public void render(float mouseX, float mouseY) {
        boolean closeHovered = mouseX >= this.x + this.width - 12.5f && mouseY >= this.y + 1.5f && mouseX <= this.x + this.width - 12.5f + 10.5f && mouseY <= this.y + 1.5f + 10.5f;
        this.closeButtonHover.setState(closeHovered);
        if (this.dragging) {
            this.x = mouseX - this.lastX;
            this.y = mouseY - this.lastY;
        }
        Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.2);
        BlurUtil.blur_shader_2.bindFramebuffer(false);
        BlurUtil.preBlur();
        RoundedUtils.glRound(this.x, this.y, this.width, this.height, 5.0f, bg.getRGB());
        BlurUtil.postBlur(6.0f, 2.0f);
        Wrapper.getMinecraft().getFramebuffer().bindFramebuffer(false);
        float g = 0.0f;
        float h = 0.0f;
        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        RoundedUtils.shadowGradient(this.x - g, this.y - h, this.width + g * 2.0f, this.height + h * 2.0f, 5.0f, 10.0f, 1.0f, ColorUtil.fadeBetween(10, 270, c1, c2), ColorUtil.fadeBetween(10, 0, c1, c2), ColorUtil.fadeBetween(10, 180, c1, c2), ColorUtil.fadeBetween(10, 90, c1, c2), false);
        RoundedUtils.round(this.x, this.y, this.width, this.height, 5.0f, bg);
        RoundedUtils.gradient(this.x + 0.5f, this.y + this.header - 1.0f, this.width - 1.0f, 2.0f, 0.0f, 0.6f, ColorUtil.fadeBetween(10, 270, c1, c2), ColorUtil.fadeBetween(10, 0, c1, c2), ColorUtil.fadeBetween(10, 180, c1, c2), ColorUtil.fadeBetween(10, 90, c1, c2));
        Wrapper.getFontUtil().entypo18.drawString(FontUtil.UNICODES_UI.NO, this.x + this.width - 10.5f, this.y + 2.5f, Color.WHITE, false);
    }

    public void mouseClicked(float mouseX, float mouseY, Click click) {
        boolean closeHovered;
        boolean bl = closeHovered = mouseX >= this.x + this.width - 12.5f && mouseY >= this.y + 1.5f && mouseX <= this.x + this.width - 12.5f + 10.5f && mouseY <= this.y + 1.5f + 10.5f;
        if (click.equals((Object)Click.LEFT) && this.mouseOverHeader(mouseX, mouseY)) {
            if (closeHovered) {
                this.shouldClose = true;
            }
            this.dragging = true;
            this.lastX = mouseX - this.x;
            this.lastY = mouseY - this.y;
        }
    }

    public void mouseReleased() {
        this.dragging = false;
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public boolean mouseOverHeader(float mouseX, float mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.header;
    }

    public boolean shouldWindowClose() {
        return this.shouldClose;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeader() {
        return this.header;
    }

    public boolean isDragging() {
        return this.dragging;
    }
}

