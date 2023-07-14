/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.menu;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.BlurUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;

public class MenuButton {
    private final ResourceLocation texture;
    private final Runnable onClick;
    private final float x;
    private float y;
    private final float width;
    private final float height;
    private final Animation hover = new Animation(() -> Float.valueOf(250.0f), false, () -> Easing.CUBIC_OUT);

    public MenuButton(String iconName, Runnable onClick, float x, float y, float width, float height) {
        this.texture = new ResourceLocation("/monsoon/mainmenu/" + iconName + ".png");
        this.onClick = onClick;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(float mouseX, float mouseY) {
        this.hover.setState(this.isHovered(mouseX, mouseY));
        String name = this.texture.getResourcePath().replace("/monsoon/mainmenu/", "").replace(".png", "");
        String let = "";
        switch (name.charAt(0)) {
            case 's': {
                let = "c";
                break;
            }
            case 'm': {
                let = "b";
                break;
            }
            case 'o': {
                let = "d";
                break;
            }
            case 'q': {
                let = "e";
            }
        }
        Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.2);
        BlurUtil.blur_shader_2.bindFramebuffer(false);
        BlurUtil.preBlur();
        RoundedUtils.glRound(this.x, (float)((double)this.y - (double)(this.height / 4.0f) * this.hover.getAnimationFactor()), this.width, this.height, 16.0f, bg.getRGB());
        BlurUtil.postBlur(2.0f, 2.0f);
        Wrapper.getMinecraft().getFramebuffer().bindFramebuffer(false);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.round(this.x, this.y, this.width, this.height, 16.0f, Color.WHITE);
        StencilUtil.bindReadStencilBuffer(1);
        Wrapper.getFont().drawCenteredString(name.substring(0, 1).toUpperCase() + name.substring(1), this.x + this.width / 2.0f, this.y + this.height + (float)(Wrapper.getFont().getHeight() / 2) - (float)((double)(Wrapper.getFont().getHeight() - 1) * this.hover.getAnimationFactor()) * 2.0f, new Color(255, 255, 255, (int)(255.0 * this.hover.getAnimationFactor())), true);
        StencilUtil.uninitStencilBuffer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 0.0f;
        float h = 0.0f;
        Color c1 = new Color(0, 238, 255, 255);
        Color c2 = new Color(135, 56, 232, 255);
        RoundedUtils.shadowGradient(this.x - g, (float)((double)this.y - (double)(this.height / 4.0f) * this.hover.getAnimationFactor() - (double)h), this.width + g * 2.0f, this.height + h * 2.0f, 16.0f, (float)this.hover.getAnimationFactor() * 7.0f, 1.0f, ColorUtil.fadeBetween(10, 270, c1, c2), ColorUtil.fadeBetween(10, 0, c1, c2), ColorUtil.fadeBetween(10, 180, c1, c2), ColorUtil.fadeBetween(10, 90, c1, c2), false);
        RoundedUtils.round(this.x, (float)((double)this.y - (double)(this.height / 4.0f) * this.hover.getAnimationFactor()), this.width, this.height, 16.0f, bg);
        Wrapper.getFontUtil().menuIcons.drawCenteredString(let, this.x + this.width / 2.0f, (float)((double)this.y - (double)(this.height / 4.0f) * this.hover.getAnimationFactor() + (double)(this.height / 2.0f)) - (float)Wrapper.getFontUtil().menuIcons.getHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, 0.5f + (float)this.hover.getAnimationFactor() / 3.0f), false);
    }

    public void mouseClicked(float mouseX, float mouseY) {
        if (this.hover.getState()) {
            this.onClick.run();
        }
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }
}

