package rip.athena.client.gui.clickgui.components.macros;

import rip.athena.client.gui.clickgui.components.mods.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import org.lwjgl.input.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class MacroSlimTextField extends SearchTextfield
{
    public MacroSlimTextField(final TextPattern pattern, final int x, final int y, final int width, final int height) {
        super(pattern, x, y, width, height);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(127, 127, 127, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(200, 200, 200, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(150, 150, 150, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(225, 225, 225, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(36, 35, 38, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(200, 200, 200, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(50, 50, 52, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(225, 225, 225, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
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
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
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
                this.drawVerticalLine(x + FontManager.getProductSansRegular(30).width(textToDraw.substring(0, toRender)) + 1, y + height / 2 - textHeight / 2, textHeight, 1, textColor);
            }
            else {
                final int textHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
                this.drawVerticalLine(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(textToDraw.substring(0, toRender)) + 1, y + height / 2 - textHeight / 2, textHeight, 1, textColor);
            }
        }
        final int renderIndex = comp;
        final int renderStopIndex = comp + textToDraw.length();
        while (this.index > this.text.length()) {
            --this.index;
        }
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(textToDraw, x, y + height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, textColor);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(textToDraw, x, y + height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, textColor);
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
                    position += Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.text.charAt(j) + "");
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
        this.mouseDown = false;
    }
}
