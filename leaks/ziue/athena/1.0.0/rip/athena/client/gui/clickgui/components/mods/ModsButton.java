package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class ModsButton extends MenuButton
{
    public ModsButton(final String text, final int x, final int y) {
        super(text, x, y, 120, 20);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(43, 43, 43, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(68, 68, 68, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(58, 58, 58, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(82, 82, 82, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(150, 150, 150, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(43, 43, 43, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(68, 68, 68, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(58, 58, 58, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(82, 82, 82, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(150, 150, 150, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(150, 150, 150, 255));
    }
    
    @Override
    public boolean passesThrough() {
        if (this.disabled) {
            return true;
        }
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        return !this.mouseDown || mouseX < x - 20 || mouseX > x + width || mouseY < y || mouseY > y + height + 1;
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = this.active ? ButtonState.ACTIVE : ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x - 20 && mouseX <= x + width && mouseY >= y && mouseY <= y + height + 1) {
                state = ButtonState.HOVER;
                if (this.active) {
                    state = ButtonState.HOVERACTIVE;
                }
                if (this.mouseDown) {
                    this.active = !this.active;
                    this.onAction();
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + height), 12.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        RoundedUtils.drawRoundedGradientOutlineCorner((float)x, (float)y, (float)(x + width), (float)(y + height), 1.0f, 12.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB());
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        this.mouseDown = false;
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
