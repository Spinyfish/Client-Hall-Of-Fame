package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.gui.clickgui.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class GoBackButton extends MenuButton
{
    public GoBackButton(final int x, final int y) {
        super("GO BACK", x, y, 120, 25);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(80, 80, 82, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(126, 126, 126, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(126, 126, 126, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(126, 126, 126, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(27, 27, 29, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(36, 36, 38, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(32, 32, 34, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(42, 42, 44, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(120, 120, 120, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(120, 120, 120, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(29, 29, 32, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(25, 25, 28, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(36, 36, 40, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, IngameMenu.MENU_ALPHA));
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
        int x = this.getRenderX();
        int y = this.getRenderY();
        int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        final int rounding = 10;
        height += 4;
        width += 4;
        x -= 2;
        y -= 2;
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        DrawUtils.drawRoundedRect(x - 4, y - 4, x + width + 5, y + height + 5, (float)rounding, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        DrawUtils.drawRoundedRect(x - 3, y - 3, x + width + 4, y + height + 4, (float)rounding, 335544320);
        DrawUtils.drawRoundedRect(x - 2, y - 2, x + width + 3, y + height + 3, (float)rounding, 436207616);
        height -= 4;
        width -= 4;
        x += 2;
        y += 2;
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        x -= 20;
        width = 30;
        height += 4;
        width += 4;
        x -= 2;
        y -= 2;
        DrawUtils.drawRoundedRect(x - 4, y - 4, x + width + 5, y + height + 5, (float)rounding, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        DrawUtils.drawRoundedRect(x - 3, y - 3, x + width + 4, y + height + 4, (float)rounding, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        DrawUtils.drawRoundedRect(x - 2, y - 2, x + width + 3, y + height + 3, (float)rounding, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        height -= 4;
        width -= 4;
        x += 2;
        y += 2;
        DrawUtils.drawRoundedRect(x - 1, y - 1, x + width + 2, y + height + 2, (float)rounding, lineColor);
        DrawUtils.drawRoundedRect(x, y, x + width + 1, y + height + 1, (float)rounding, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width, y + height, (float)rounding, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
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
