package rip.athena.client.gui.clickgui.components.fps;

import net.minecraft.util.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class OptifineParentBackground extends MenuComponent
{
    protected ResourceLocation image;
    protected String topText;
    protected String botText;
    
    public OptifineParentBackground(final ResourceLocation image, final String topText, final String botText, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.image = image;
        this.topText = topText;
        this.botText = botText;
        this.setPriority(MenuPriority.LOW);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int width = this.width;
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        RoundedUtils.drawRoundedOutline((float)x, (float)y, (float)(x + width), (float)(y + this.height), 24.0f, 5.0f, new Color(50, 50, 50, 255).getRGB());
        RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + this.height), 24.0f, new Color(30, 30, 30, 255).getRGB());
        this.drawText(this.topText, x + 15, y + 15, textColor);
        this.drawText(this.botText, x + width / 2 - this.getStringWidth(this.botText) / 2, y + this.height - 60, textColor);
        final int imgRad = 120;
        this.drawImage(this.image, x + 40, y + 60, imgRad, imgRad);
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
