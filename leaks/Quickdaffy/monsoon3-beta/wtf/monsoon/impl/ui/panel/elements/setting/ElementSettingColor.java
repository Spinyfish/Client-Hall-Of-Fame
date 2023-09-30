/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.impl.ui.panel.elements.setting;

import java.awt.Color;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.primitive.Click;

public class ElementSettingColor
extends ElementSetting<Color> {
    private boolean dragging;
    private boolean sliding;

    public ElementSettingColor(Setting<Color> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        if (!Mouse.isButtonDown((int)0)) {
            this.sliding = false;
            this.dragging = false;
        }
        Color originalSetColor = (Color)this.getSetting().getValue();
        float[] hsb = new float[3];
        Color.RGBtoHSB(originalSetColor.getRed(), originalSetColor.getGreen(), originalSetColor.getBlue(), hsb);
        float pickerX = this.getX() + 4.0f;
        float pickerY = this.getY() + 18.0f;
        float pickerWidth = this.getWidth() - 8.0f;
        float pickerHeight = this.getHeight() - 30.0f;
        float RAWpickerTargetX = Math.min(Math.max(0.0f, pickerWidth + (pickerX - mouseX)), pickerWidth);
        float RAWpickerTargetY = Math.min(Math.max(0.0f, pickerHeight + (pickerY - mouseY)), pickerHeight);
        float pickerTargetX = RAWpickerTargetX / pickerWidth;
        float pickerTargetY = RAWpickerTargetY / pickerHeight;
        float hueX = pickerX;
        float hueY = this.getY() + this.getHeight() - 8.0f;
        float hueWidth = pickerWidth;
        float hueHeight = 4.0f;
        float RAWhueTargetX = RAWpickerTargetX;
        float hueTargetX = RAWhueTargetX / hueWidth;
        Color hueCol = new Color(Color.HSBtoRGB(hsb[0], 1.0f, 1.0f));
        float[] hsb2 = Color.RGBtoHSB(((Color)this.getSetting().getValue()).getRed(), ((Color)this.getSetting().getValue()).getGreen(), ((Color)this.getSetting().getValue()).getBlue(), null);
        float[] newHSB = new float[]{this.sliding ? hueTargetX : 1.0f - hsb2[0], this.dragging ? pickerTargetX : 1.0f - hsb2[1], this.dragging ? pickerTargetY : hsb2[2]};
        this.getSetting().setValue(new Color(Color.HSBtoRGB(1.0f - newHSB[0], 1.0f - newHSB[1], newHSB[2])));
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(this.getX() + 1.0f, this.getY(), this.getWidth() - 2.0f, this.getHeight(), new Color(0x252525));
        RoundedUtils.gradient(pickerX, pickerY, pickerWidth, pickerHeight, 4.0f, 1.0f, Color.WHITE, Color.BLACK, hueCol, Color.BLACK);
        int i = 0;
        while ((float)i < hueWidth) {
            RenderUtil.rect(hueX + (float)i, hueY, 1.0f, hueHeight, new Color(Color.HSBtoRGB((float)i / (this.getWidth() - 8.0f), 1.0f, 1.0f)));
            ++i;
        }
        RenderUtil.rect(hueX + hueWidth * (hsb[0] - 0.0027777778f), hueY, 1.0f, hueHeight, Color.WHITE);
        float circleWidth = 3.0f;
        RoundedUtils.circle(pickerX + hsb2[1] * pickerWidth, pickerY + (1.0f - hsb2[2]) * pickerHeight, circleWidth, Color.WHITE);
        Wrapper.getFontUtil().productSansSmall.drawString(this.getSetting().getName(), this.getX() + 4.0f, this.getY() + 4.0f, new Color(-7368817), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        float pickerX = this.getX() + 4.0f;
        float pickerY = this.getY() + 18.0f;
        float pickerWidth = this.getWidth() - 8.0f;
        float pickerHeight = this.getHeight() - 30.0f;
        float hueX = pickerX;
        float hueY = this.getY() + this.getHeight() - 8.0f;
        float hueWidth = pickerWidth;
        float hueHeight = 4.0f;
        if (mouseX >= pickerX && mouseY >= pickerY && mouseX <= pickerX + pickerWidth && mouseY <= pickerY + pickerHeight && click.equals((Object)Click.LEFT)) {
            this.dragging = true;
        }
        if (mouseX >= hueX && mouseY >= hueY && mouseX <= hueX + hueWidth && mouseY <= hueY + hueHeight && click.equals((Object)Click.LEFT)) {
            this.sliding = true;
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }
}

