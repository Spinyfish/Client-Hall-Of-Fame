package rip.athena.client.gui.clickgui.components.macros;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class FlipButton extends MenuButton
{
    public static final int NORMAL_ON;
    public static final int NORMAL_OFF;
    public static final int HOVER_ON;
    public static final int HOVER_OFF;
    
    public FlipButton(final int x, final int y, final int width, final int height) {
        super("", x, y, width, height);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(28, 28, 30, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onRender() {
        int x = this.getRenderX();
        int y = this.getRenderY();
        int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int linePopupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        x += 10;
        width -= 20;
        y += 5;
        height -= 10;
        DrawUtils.drawRoundedRect(x - 1, y - 1, x + width + 1, y + height + 1, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, new Color(35, 35, 35, 255).getRGB());
        int color = this.active ? FlipButton.NORMAL_ON : FlipButton.NORMAL_OFF;
        if (this.lastState == ButtonState.HOVER || this.lastState == ButtonState.HOVERACTIVE) {
            color = (this.active ? FlipButton.HOVER_ON : FlipButton.HOVER_OFF);
        }
        ++x;
        ++y;
        --width;
        --height;
        if (this.active) {
            DrawUtils.drawRoundedRect(x, y, x + width / 2, y + height, 4.0f, color);
        }
        else {
            DrawUtils.drawRoundedRect(x + width - width / 2, y, x + width, y + height, 4.0f, color);
        }
        this.mouseDown = false;
    }
    
    @Override
    public void drawText(final String string, final int x, final int y, final int color) {
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(string, x, y, color);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(string, x, y, color);
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
    
    static {
        NORMAL_ON = new Color(15, 58, 28, IngameMenu.MENU_ALPHA).getRGB();
        NORMAL_OFF = new Color(58, 15, 17, IngameMenu.MENU_ALPHA).getRGB();
        HOVER_ON = new Color(23, 92, 44, IngameMenu.MENU_ALPHA).getRGB();
        HOVER_OFF = new Color(94, 25, 28, IngameMenu.MENU_ALPHA).getRGB();
    }
}
