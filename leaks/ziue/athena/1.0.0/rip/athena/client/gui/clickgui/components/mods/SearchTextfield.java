package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import org.lwjgl.input.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;
import rip.athena.client.*;
import net.minecraft.util.*;
import rip.athena.client.utils.render.*;

public class SearchTextfield extends MenuTextField
{
    protected static final Color PLACEHOLDER_COLOR;
    
    public SearchTextfield(final TextPattern pattern, final int x, final int y, final int width, final int height) {
        super(pattern, x, y, width, height);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(27, 27, 29, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(36, 36, 38, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(32, 32, 34, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(42, 42, 44, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(120, 120, 120, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(29, 29, 32, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(25, 25, 28, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(36, 36, 40, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + this.minOffset * 2;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        if (this.tab && !Keyboard.isKeyDown(15)) {
            this.tab = false;
        }
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawRoundedRect((float)(x - 4), (float)(y - 4), (float)(x + width + 5), (float)(y + height + 5), 24.0f, 83886080);
        RoundedUtils.drawRoundedRect((float)(x - 2), (float)(y - 2), (float)(x + width + 3), (float)(y + height + 3), 24.0f, 587202560);
        String textToDraw = this.text;
        if (this.isPasswordField()) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < textToDraw.length(); ++i) {
                builder.append("*");
            }
            textToDraw = builder.toString();
        }
        boolean drawPointer = false;
        if (this.focused && (System.currentTimeMillis() - this.lineTime) % this.lineRefreshTime * 2L >= this.lineRefreshTime) {
            drawPointer = true;
        }
        int labelWidth;
        if (Settings.customGuiFont) {
            labelWidth = FontManager.getProductSansRegular(30).width(textToDraw + 1);
        }
        else {
            labelWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw + 1);
        }
        int comp = 0;
        int toRender = this.index;
        while (labelWidth >= width) {
            if (comp < this.index) {
                textToDraw = textToDraw.substring(1);
                if (Settings.customGuiFont) {
                    labelWidth = FontManager.getProductSansRegular(30).width(textToDraw + 1);
                }
                else {
                    labelWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw + 1);
                }
                --toRender;
            }
            else if (comp > this.index) {
                textToDraw = textToDraw.substring(0, textToDraw.length() - 1);
                if (Settings.customGuiFont) {
                    labelWidth = FontManager.getProductSansRegular(30).width(textToDraw + 1);
                }
                else {
                    labelWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw + 1);
                }
            }
            ++comp;
        }
        if (drawPointer) {
            if (toRender > textToDraw.length()) {
                toRender = textToDraw.length() - 1;
            }
            if (toRender < 0) {
                toRender = 0;
            }
            if (Settings.customGuiFont) {
                final int textHeight = (int)rip.athena.client.font.FontManager.baloo17.getHeight(textToDraw);
                this.drawVerticalLine(x + 10 + FontManager.getNunito(20).width(textToDraw.substring(0, toRender)) + 1, y + height / 2 - textHeight / 2, textHeight, 1, textColor);
            }
            else {
                final int textHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
                this.drawVerticalLine(x + 10 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw.substring(0, toRender)) + 1, y + height / 2 - textHeight / 2, textHeight, 1, textColor);
            }
        }
        final int renderIndex = comp;
        final int renderStopIndex = comp + textToDraw.length();
        while (this.index > this.text.length()) {
            --this.index;
        }
        int xAdd = 0;
        if (textToDraw.isEmpty() && !this.isFocused()) {
            textToDraw = "SEARCH MODS...";
            xAdd = 5;
            textColor = SearchTextfield.PLACEHOLDER_COLOR.getRGB();
        }
        if (Settings.customGuiFont) {
            FontManager.getNunito(20).drawString(textToDraw, x + 10 + this.minOffset + xAdd, y + 2 + height / 2.0f - (int)FontManager.getNunito(20).height() / 2.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(textToDraw, x + 10 + this.minOffset + xAdd, (int)(y + height / 2.0f - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2.0f), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        if (this.lastState == ButtonState.HOVER && this.mouseDown) {
            this.focused = true;
            this.lineTime = this.getLinePrediction();
            int position = x;
            if (mouseX < position) {
                this.index = 0;
                return;
            }
            float bestDiff = 1000.0f;
            int bestIndex = -1;
            for (int j = renderIndex; j < renderStopIndex; ++j) {
                if (this.text.length() > j) {
                    final int diff = Math.abs(mouseX - position);
                    if (bestDiff > diff) {
                        bestDiff = (float)diff;
                        bestIndex = j;
                    }
                    position += FontManager.getNunito(20).width(this.text.charAt(j) + "");
                }
            }
            if (mouseX > position) {
                this.index = this.text.length();
            }
            else if (bestIndex != -1) {
                this.index = bestIndex;
            }
            else {
                this.index = 0;
            }
        }
        DrawUtils.drawImage(new ResourceLocation("Athena/gui/menu/search.png"), x + width - 30, y + 5, 20, 20);
        this.mouseDown = false;
    }
    
    static {
        PLACEHOLDER_COLOR = new Color(80, 80, 82, IngameMenu.MENU_ALPHA);
    }
}
