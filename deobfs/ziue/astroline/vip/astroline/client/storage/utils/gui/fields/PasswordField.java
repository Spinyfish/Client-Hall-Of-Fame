/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiTextField
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.util.MathHelper
 *  org.lwjgl.input.Mouse
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.storage.utils.gui.fields;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class PasswordField
extends GuiTextField {
    private int color;
    private int textColor;

    public PasswordField(int componentId, FontRenderer fontRenderer, int x, int y, int width, int height, String shit) {
        super(componentId, fontRenderer, x, y, width, height);
    }

    public void drawTextBox() {
        if (!this.getVisible()) return;
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int lmx = scaledresolution.getScaledWidth();
        int imy = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * lmx / Minecraft.getMinecraft().displayWidth;
        int mouseY = imy - Mouse.getY() * imy / Minecraft.getMinecraft().displayHeight - 1;
        boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        RenderUtil.drawBorderedRect((float)this.xPosition, (float)this.yPosition, (float)(this.xPosition + this.width), (float)(this.yPosition + this.height), (float)0.1f, (int)(this.color == -1 && (hovered || this.isFocused) ? new Color(0, 0, 0, 100).getRGB() : new Color(0, 0, 0, 50).getRGB()), (int)new Color(0, 0, 0, 50).getRGB());
        FontManager.normal2.drawString("Password", (float)(this.xPosition + 5), (float)(this.yPosition + 5), this.textColor);
        int i = this.isEnabled ? this.enabledColor : this.disabledColor;
        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        String s = FontManager.normal2.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
        int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
        int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
        int j1 = l;
        if (k > s.length()) {
            k = s.length();
        }
        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = FontManager.normal2.drawString(s1, (float)(this.xPosition + 5), (float)(this.yPosition + 17), this.textColor);
        }
        boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? l + this.width : l;
        } else if (flag2) {
            k1 = j1 - 1;
            --j1;
        }
        if (!s.isEmpty() && flag && j < s.length()) {
            FontManager.normal2.drawString(s.substring(j), (float)j1, (float)i1, i);
        }
        if (flag1) {
            if (flag2) {
                Gui.drawRect((int)k1, (int)(i1 - 1), (int)(k1 + 1), (int)(i1 + 1 + this.fontRendererInstance.FONT_HEIGHT), (int)-3092272);
            } else {
                Minecraft.getMinecraft().fontRendererObj.drawString("_", (float)(this.xPosition + 5) + (this.getText().isEmpty() ? 0.0f : FontManager.normal2.getStringWidth(this.getText()) + 1.0f), (float)(this.yPosition + 17), i, true);
            }
        }
        if (k == j) return;
        int l1 = l + Minecraft.getMinecraft().fontRendererObj.getStringWidth(s.substring(0, k));
        this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_) {
        boolean flag;
        boolean bl = flag = p_146192_1_ >= this.xPosition && p_146192_1_ < this.xPosition + this.width && p_146192_2_ >= this.yPosition && p_146192_2_ < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }
        if (!this.isFocused) return;
        if (!flag) return;
        if (p_146192_3_ != 0) return;
        int i = p_146192_1_ - this.xPosition;
        if (!this.enableBackgroundDrawing) return;
        i -= 4;
    }

    public void setSelectionPos(int p_146199_1_) {
        int i = this.text.length();
        if (p_146199_1_ > i) {
            p_146199_1_ = i;
        }
        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }
        this.selectionEnd = p_146199_1_;
        if (this.lineScrollOffset > i) {
            this.lineScrollOffset = i;
        }
        int j = this.getWidth();
        String s = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
        int k = s.length() + this.lineScrollOffset;
        if (p_146199_1_ == this.lineScrollOffset) {
            this.lineScrollOffset -= Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text, j, true).length();
        }
        if (p_146199_1_ > k) {
            this.lineScrollOffset += p_146199_1_ - k;
        } else if (p_146199_1_ <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - p_146199_1_;
        }
        this.lineScrollOffset = MathHelper.clamp_int((int)this.lineScrollOffset, (int)0, (int)i);
    }

    public void updateCoordinates(float x, float y) {
        this.xPosition = (int)x;
        this.yPosition = (int)y;
    }
}
