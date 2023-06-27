package rip.athena.client.gui.clickgui.components.macros;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class MacroButton extends MenuButton
{
    private boolean approve;
    
    public MacroButton(final String text, final int x, final int y, final int width, final int height, final boolean approve) {
        super(text, x, y, width, height);
        this.approve = approve;
        this.onInitColors();
    }
    
    @Override
    public void onInitColors() {
        if (this.approve) {
            this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(0, 200, 0, 255));
            this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(50, 112, 29, 255));
            this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(47, 105, 27, 255));
            this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(60, 133, 34, 255));
            this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        }
        else {
            this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(200, 0, 0, 255));
            this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(99, 15, 18, 255));
            this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(92, 16, 18, 255));
            this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(110, 19, 21, 255));
            this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        }
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, lineColor);
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width - 1, y + height - 1, 4.0f, new Color(35, 35, 35, 255).getRGB());
        this.drawText(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + (height / 2 - this.getStringHeight(this.text) / 2), textColor);
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
    
    public void setApprove(final boolean approve) {
        this.approve = approve;
        this.onInitColors();
    }
}
