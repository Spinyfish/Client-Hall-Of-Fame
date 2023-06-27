package rip.athena.client.gui.clickgui.components.themes.accent;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.theme.impl.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class SimpleGradientButton extends MenuButton
{
    protected boolean leftColorChange;
    
    public SimpleGradientButton(final AccentTheme theme, final int x, final int y, final int width, final int height, final boolean leftColorChange) {
        super(theme, x, y, width, height);
        this.leftColorChange = leftColorChange;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(52, 52, 53, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(28, 28, 30, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(56, 56, 58, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(90, 90, 94, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(75, 75, 78, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(100, 100, 104, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int width = this.width;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        final int linePopupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawGradientRound((float)x, (float)y, (float)width, (float)this.height, 6.0f, this.theme.getFirstColor(), this.theme.getFirstColor(), this.theme.getSecondColor(), this.theme.getSecondColor());
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.mouseDown = false;
    }
    
    @Override
    public void drawText(final String string, final int x, final int y, final int color) {
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(string, x - 3, y, color);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(string, x, y, color);
        }
    }
    
    @Override
    public int getStringWidth(final String string) {
        if (Settings.customGuiFont) {
            return FontManager.getProductSansRegular(30).width(string) - 1;
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
    }
    
    @Override
    public int getStringHeight(final String string) {
        if (Settings.customGuiFont) {
            return (int)rip.athena.client.font.FontManager.baloo17.getHeight(string) + 1;
        }
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
}
