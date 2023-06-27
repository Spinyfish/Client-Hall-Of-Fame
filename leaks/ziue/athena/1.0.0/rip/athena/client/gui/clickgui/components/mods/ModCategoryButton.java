package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import net.minecraft.util.*;
import rip.athena.client.modules.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.*;
import net.minecraft.client.*;
import rip.athena.client.utils.render.*;

public class ModCategoryButton extends MenuButton
{
    public static final int MAIN_COLOR;
    private final int IMAGE_SIZE = 22;
    private ResourceLocation image;
    
    public ModCategoryButton(final Category category, final int x, final int y, final int width, final int height) {
        super(category.getText(), x, y, width, height);
    }
    
    public ModCategoryButton(final String text, final int x, final int y, final int width, final int height) {
        super(text, x, y, width, height);
    }
    
    public ModCategoryButton(final String text, final ResourceLocation resourceLocation, final int x, final int y, final int width, final int height) {
        super(text, x, y, width, height);
        this.image = resourceLocation;
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(25, 25, 25, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(20, 20, 20, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(35, 35, 39, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        if (this.isActive()) {
            RoundedUtils.drawRound((float)(x + 17), (float)y, (float)(width - 23), (float)(height - 4), 12.0f, new Color(10, 10, 10, 150));
            RoundedUtils.drawRoundedGradientOutlineCorner((float)(x + 17), (float)y, (float)(width + x - 7), (float)(height + y - 4), 1.0f, 12.0f, ColorUtil.getClientColor(0, 255).getRGB(), ColorUtil.getClientColor(90, 255).getRGB(), ColorUtil.getClientColor(180, 255).getRGB(), ColorUtil.getClientColor(270, 255).getRGB());
        }
        if (Settings.customGuiFont) {
            if (this.text.equalsIgnoreCase("EDIT HUD")) {
                FontManager.getProductSansBold(35).drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2) - 15, y + height / 2 - this.getStringHeight(this.text) / 2 - 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
            }
            else {
                FontManager.getProductSansBold(25).drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2 - 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
            }
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + height / 2 - this.getStringHeight(this.text) / 2 - 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        if (this.text.equalsIgnoreCase("EDIT HUD")) {
            DrawUtils.drawImage(this.image, x + 25, y + 3, 25, 25);
        }
        this.mouseDown = false;
    }
    
    @Override
    public int getStringWidth(final String string) {
        if (Settings.customGuiFont) {
            return FontManager.getNunitoBold(20).width(string);
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
        MAIN_COLOR = new Color(35, 35, 35, IngameMenu.MENU_ALPHA).getRGB();
    }
}
