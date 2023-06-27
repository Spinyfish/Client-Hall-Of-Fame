package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.*;
import net.minecraft.client.*;

public class FeatureText extends MenuLabel
{
    public FeatureText(final String text, final String tooltip, final int x, final int y) {
        super(text, tooltip, x, y);
    }
    
    public FeatureText(final String text, final int x, final int y) {
        super(text, x, y);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void drawText(final String text, final int x, final int y, final int color) {
        if (Settings.customGuiFont) {
            FontManager.getNunitoBold(30).drawString(text, x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
    }
    
    @Override
    public int getStringWidth(final String text) {
        if (Settings.customGuiFont) {
            return FontManager.getNunitoBold(30).width(text);
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }
    
    @Override
    public int getStringHeight(final String text) {
        return (int)rip.athena.client.font.FontManager.baloo17.getHeight(text);
    }
}
