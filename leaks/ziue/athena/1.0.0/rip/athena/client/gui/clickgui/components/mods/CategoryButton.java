package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import net.minecraft.util.*;
import rip.athena.client.gui.clickgui.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.*;
import net.minecraft.client.*;
import rip.athena.client.utils.render.*;

public class CategoryButton extends MenuButton
{
    private ResourceLocation image;
    
    public CategoryButton(final Category category, final int x, final int y) {
        super(category.getName(), x, y);
    }
    
    public CategoryButton(final Category category, final int x, final int y, final int width, final int height) {
        super(category.getName(), x, y, width, height);
    }
    
    public CategoryButton(final Category category, final ResourceLocation image, final int x, final int y, final int width, final int height) {
        super(category.getName(), x, y, width, height);
        this.image = image;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(30, 30, 30, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(30, 30, 30, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(25, 25, 25, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(20, 20, 20, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
        super.onInitColors();
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        if (this.isActive()) {
            RoundedUtils.drawRound((float)(x + 30), (float)y, (float)(width - 70), (float)height, 12.0f, new Color(10, 10, 10, 150));
            RoundedUtils.drawRoundedGradientOutlineCorner((float)(x + 30), (float)y, (float)(width + x - 40), (float)(height + y), 1.0f, 12.0f, ColorUtil.getClientColor(0, 255).getRGB(), ColorUtil.getClientColor(90, 255).getRGB(), ColorUtil.getClientColor(180, 255).getRGB(), ColorUtil.getClientColor(270, 255).getRGB());
        }
        if (Settings.customGuiFont) {
            FontManager.getNunitoBold(25).drawString(this.text, x + 70, y + height / 2 - this.getStringHeight(this.text) / 2 + 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        DrawUtils.drawImage(this.image, x + 35, y + 3, 25, 25);
        this.mouseDown = false;
    }
    
    @Override
    public int getStringWidth(final String string) {
        if (Settings.customGuiFont) {
            return FontManager.getNunitoBold(25).width(string);
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
    }
    
    @Override
    public int getStringHeight(final String string) {
        if (Settings.customGuiFont) {
            return (int)FontManager.getNunitoBold(25).height();
        }
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
}
