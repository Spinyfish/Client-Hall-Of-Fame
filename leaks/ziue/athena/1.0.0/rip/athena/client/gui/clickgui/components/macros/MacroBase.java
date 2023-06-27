package rip.athena.client.gui.clickgui.components.macros;

import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class MacroBase extends MenuComponent
{
    protected String text;
    protected int spacing;
    protected int textWidth;
    
    public MacroBase(final String text, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.spacing = 15;
        this.textWidth = 165;
        final StringBuilder builder = new StringBuilder();
        for (final char character : text.toUpperCase().toCharArray()) {
            if (this.getStringWidth(builder.toString() + character) > this.textWidth - this.spacing * 2) {
                break;
            }
            builder.append(character);
        }
        this.text = builder.toString();
        this.setPriority(MenuPriority.LOW);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(68, 67, 69, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int width = this.width;
        final int linePopupColor = this.getColor(DrawType.LINE, ButtonState.POPUP);
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + this.height), 12.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getThirdColor());
        this.drawText(this.text, x + this.spacing, y + this.height / 2 - this.getStringHeight(this.text) / 2, -1);
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
}
