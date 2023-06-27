package rip.athena.client.gui.clickgui.components.waypoints;

import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class WaypointTextBarrier extends MenuComponent
{
    protected String text;
    
    public WaypointTextBarrier(final String text, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.text = text;
        this.setPriority(MenuPriority.HIGHEST);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(66, 66, 68, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        DrawUtils.drawRoundedRect(x, y, x + this.width, y + this.height, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + this.width - 1, y + this.height - 1, 4.0f, new Color(35, 35, 35, 255).getRGB());
        this.drawText(this.text + "", x + this.width / 2 - this.getStringWidth(this.text) / 2 + 2, y + this.height / 2 - this.getStringHeight(this.text) / 2, textColor);
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
    
    public String getText() {
        return this.text;
    }
}
