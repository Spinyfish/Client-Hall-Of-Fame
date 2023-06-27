package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class MenuModList extends MenuDropdown
{
    protected int cursorWidth;
    
    public MenuModList(final String[] values, final int x, final int y, final int height) {
        super(values, x, y);
        this.cursorWidth = 25;
        this.height = height;
        this.width += this.cursorWidth * 2;
    }
    
    public MenuModList(final Class<?> values, final int x, final int y, final int height) {
        super(values, x, y);
        this.cursorWidth = 25;
        this.height = height;
        this.width += this.cursorWidth * 2;
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(162, 162, 162, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(182, 182, 182, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(24, 24, 27, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(53, 53, 55, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(120, 120, 120, 255));
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + this.textOffset;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (!this.disabled) {
            final boolean inRange = false;
            if (mouseX >= x && mouseX <= x + width + this.arrowOffset - 1 && mouseY >= y && mouseY <= y + this.height + 1) {
                state = ButtonState.HOVER;
                if (this.mouseDown) {
                    if (mouseX < x + this.cursorWidth - 1) {
                        if (this.index - 1 >= 0) {
                            --this.index;
                        }
                        else {
                            this.index = this.values.length - 1;
                        }
                        this.onAction();
                    }
                    else if (mouseX > x + width - this.cursorWidth - 1) {
                        if (this.index + 1 < this.values.length) {
                            ++this.index;
                        }
                        else {
                            this.index = 0;
                        }
                        this.onAction();
                    }
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (state == ButtonState.HOVER || state == ButtonState.HOVERACTIVE) {
            this.setPriority(MenuPriority.HIGH);
        }
        else {
            this.setPriority(MenuPriority.MEDIUM);
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + this.textOffset + this.arrowOffset + 1;
        final int height = this.height;
        final int popupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + height), 12.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        RoundedUtils.drawRoundedGradientOutlineCorner((float)x, (float)y, (float)(x + width), (float)(y + height), 1.0f, 12.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB());
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        final int defBg = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        final int cache = backgroundColor;
        if (mouseX > x + this.cursorWidth - 1) {
            backgroundColor = defBg;
        }
        this.drawText("<", x + this.cursorWidth / 2 - this.getStringWidth("<") / 2, y + height / 2 - this.getStringHeight("<") / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        if (mouseX < x + width - this.cursorWidth - 1) {
            backgroundColor = defBg;
        }
        else {
            backgroundColor = cache;
        }
        this.drawText(">", x + width - this.cursorWidth + 3 + this.cursorWidth / 2 - this.getStringWidth(">") / 2, y + height / 2 - this.getStringHeight(">") / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        final String text = this.values[this.index].toUpperCase();
        this.drawText(text, x + width / 2 - this.getStringWidth(text) / 2, y + height / 2 - this.getStringHeight(text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        this.mouseDown = false;
    }
    
    @Override
    public void drawText(final String text, final int x, final int y, final int color) {
        if (Settings.customGuiFont) {
            FontManager.getNunito(25).drawString(text, x, y, color);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
        }
    }
    
    @Override
    public int getStringWidth(final String string) {
        if (Settings.customGuiFont) {
            return FontManager.getNunito(25).width(string);
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
