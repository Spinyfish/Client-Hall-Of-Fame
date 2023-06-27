package rip.athena.client.gui.clickgui.components.capes;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.cosmetics.cape.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class CapeButton extends MenuButton
{
    private boolean selected;
    
    public CapeButton(final Cape cape, final int x, final int y, final int width, final int height) {
        super(cape, x, y, width, height);
        this.selected = false;
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
        final int width = this.width;
        RoundedUtils.drawGradientRound((float)x, (float)y, (float)width, (float)this.height, 18.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
        RoundedUtils.drawRound((float)(x + 1), (float)(y + 1), (float)(width - 2), (float)(this.height - 2), 16.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getThirdColor()));
        if (!this.cape.getName().equals("None")) {
            DrawUtils.drawImage(this.cape.getDisplayTexture(), x + 25, y + 15, width - 50, this.height - 50);
        }
        if (this.selected) {
            this.text = this.cape.getName() + " selected";
        }
        else {
            this.text = this.cape.getName();
        }
        this.drawText(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + this.height - 20, -1);
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
    
    public void setSelected(final boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return this.selected;
    }
}
