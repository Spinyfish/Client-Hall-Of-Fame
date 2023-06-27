package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import org.lwjgl.input.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class MenuModSlider extends MenuSlider
{
    public MenuModSlider(final double startValue, final double minValue, final double maxValue, final int precision, final int x, final int y, final int width, final int height) {
        super((float)startValue, (float)minValue, (float)maxValue, precision, x, y, width, height);
    }
    
    public MenuModSlider(final float startValue, final float minValue, final float maxValue, final int precision, final int x, final int y, final int width, final int height) {
        super(startValue, minValue, maxValue, precision, x, y, width, height);
    }
    
    public MenuModSlider(final int startValue, final int minValue, final int maxValue, final int x, final int y, final int width, final int height) {
        super(startValue, minValue, maxValue, x, y, width, height);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(162, 162, 162, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(231, 27, 44, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(100, 40, 40, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(239, 46, 90, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(231, 27, 44, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(35, 35, 35, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(62, 62, 62, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(78, 78, 78, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(69, 69, 69, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(90, 90, 90, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(150, 150, 150, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(120, 120, 120, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        final int linePopupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        final int backgroundPopupColor = this.getColor(DrawType.BACKGROUND, ButtonState.POPUP);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + height), 12.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        RoundedUtils.drawRoundedRect((float)(x + 1), (float)(y + 1), (float)(x + width - 1), (float)(y + height - 1), 12.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        String data = "";
        if (this.isFloat) {
            data = this.getValue() + "/" + this.getMaxValue();
        }
        else {
            data = this.getIntValue() + "/" + Math.round(this.getMaxValue());
        }
        final float diff = this.maxValue - this.minValue;
        int linePos = Math.round((width + 1) * (this.value - this.minValue) / diff);
        if (linePos + 1 >= width) {
            linePos -= this.minOffset;
        }
        else if (x + linePos - 1 <= x) {
            linePos += this.minOffset;
        }
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + linePos, y + height - 1, 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB());
        int cursorPos = linePos;
        final int cursorWidth = 20;
        if (cursorPos < cursorWidth) {
            cursorPos = cursorWidth;
        }
        DrawUtils.drawRoundedRect(x + cursorPos - cursorWidth, y + 1, x + cursorPos, y + height - 1, 6.0f, -1);
        if (this.wantToDrag || (this.mouseDown && this.lastState == ButtonState.HOVER)) {
            if (this.mouseDown) {
                this.wantToDrag = true;
            }
            float wantedValue = this.minValue + (mouseX - this.minOffset - x) * diff / (width - this.minOffset * 2);
            if (wantedValue > this.maxValue) {
                wantedValue = this.maxValue;
            }
            else if (this.minValue > wantedValue) {
                wantedValue = this.minValue;
            }
            final float oldValue = this.value;
            this.value = wantedValue;
            if (oldValue != this.value) {
                this.onAction();
            }
        }
        if (this.wantToDrag) {
            this.mouseDragging = Mouse.isButtonDown(0);
            this.wantToDrag = this.mouseDragging;
        }
        this.mouseDragging = false;
        this.mouseDown = false;
    }
    
    @Override
    public void drawText(final String text, final int x, final int y, final int color) {
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(text, x, y, color);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
        }
    }
    
    @Override
    public int getStringWidth(final String string) {
        if (Settings.customGuiFont) {
            return FontManager.getProductSansRegular(30).width(string);
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
    }
    
    @Override
    public int getStringHeight(final String string) {
        if (Settings.customGuiFont) {
            return (int)rip.athena.client.font.FontManager.baloo17.getHeight(string);
        }
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
}
