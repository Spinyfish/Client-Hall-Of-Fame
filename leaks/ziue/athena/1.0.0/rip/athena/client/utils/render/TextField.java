package rip.athena.client.utils.render;

import rip.athena.client.utils.font.*;
import java.awt.*;
import rip.athena.client.utils.time.*;
import rip.athena.client.utils.animations.impl.*;
import net.minecraft.util.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import rip.athena.client.utils.animations.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;

public class TextField extends Gui
{
    public FontManager font;
    private float xPosition;
    private float yPosition;
    private float radius;
    private float alpha;
    private float width;
    private float height;
    private float textAlpha;
    private Color outline;
    private Color fill;
    private Color focusedTextColor;
    private Color unfocusedTextColor;
    private String text;
    private String backgroundText;
    private int maxStringLength;
    private boolean drawingBackground;
    private boolean canLoseFocus;
    private boolean isFocused;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private final Animation textColor;
    private final Animation cursorBlinkAnimation;
    private final TimerUtil timerUtil;
    private boolean visible;
    
    public TextField() {
        this.radius = 2.0f;
        this.alpha = 1.0f;
        this.textAlpha = 1.0f;
        this.outline = Color.WHITE;
        this.fill = ColorUtil.tripleColor(32);
        this.focusedTextColor = new Color(224, 224, 224);
        this.unfocusedTextColor = new Color(130, 130, 130);
        this.text = "";
        this.maxStringLength = 32;
        this.drawingBackground = true;
        this.canLoseFocus = true;
        this.textColor = new DecelerateAnimation(250, 1.0);
        this.cursorBlinkAnimation = new DecelerateAnimation(750, 1.0);
        this.timerUtil = new TimerUtil();
        this.visible = true;
    }
    
    public TextField(final FontManager font, final float x, final float y, final float par5Width, final float par6Height) {
        this.radius = 2.0f;
        this.alpha = 1.0f;
        this.textAlpha = 1.0f;
        this.outline = Color.WHITE;
        this.fill = ColorUtil.tripleColor(32);
        this.focusedTextColor = new Color(224, 224, 224);
        this.unfocusedTextColor = new Color(130, 130, 130);
        this.text = "";
        this.maxStringLength = 32;
        this.drawingBackground = true;
        this.canLoseFocus = true;
        this.textColor = new DecelerateAnimation(250, 1.0);
        this.cursorBlinkAnimation = new DecelerateAnimation(750, 1.0);
        this.timerUtil = new TimerUtil();
        this.visible = true;
        this.font = font;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
    }
    
    public void setText(final String text) {
        if (text.length() > this.maxStringLength) {
            this.text = text.substring(0, this.maxStringLength);
        }
        else {
            this.text = text;
        }
        this.setCursorPositionZero();
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getSelectedText() {
        final int i = Math.min(this.cursorPosition, this.selectionEnd);
        final int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }
    
    public void writeText(final String text) {
        String s = "";
        final String s2 = ChatAllowedCharacters.filterAllowedCharacters(text);
        final int min = Math.min(this.cursorPosition, this.selectionEnd);
        final int max = Math.max(this.cursorPosition, this.selectionEnd);
        final int len = this.maxStringLength - this.text.length() - (min - max);
        if (this.text.length() > 0) {
            s += this.text.substring(0, min);
        }
        int l;
        if (len < s2.length()) {
            s += s2.substring(0, len);
            l = len;
        }
        else {
            s += s2;
            l = s2.length();
        }
        if (this.text.length() > 0 && max < this.text.length()) {
            s += this.text.substring(max);
        }
        this.text = s;
        this.moveCursorBy(min - this.selectionEnd + l);
    }
    
    public void deleteWords(final int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            }
            else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }
    
    public void deleteFromCursor(final int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            }
            else {
                final boolean negative = num < 0;
                final int i = negative ? (this.cursorPosition + num) : this.cursorPosition;
                final int j = negative ? this.cursorPosition : (this.cursorPosition + num);
                String s = "";
                if (i >= 0) {
                    s = this.text.substring(0, i);
                }
                if (j < this.text.length()) {
                    s += this.text.substring(j);
                }
                this.text = s;
                if (negative) {
                    this.moveCursorBy(num);
                }
            }
        }
    }
    
    public int getNthWordFromCursor(final int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }
    
    public int getNthWordFromPos(final int n, final int pos) {
        return this.func_146197_a(n, pos);
    }
    
    public int func_146197_a(final int n, final int pos) {
        int i = pos;
        final boolean negative = n < 0;
        for (int j = Math.abs(n), k = 0; k < j; ++k) {
            if (!negative) {
                final int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1) {
                    i = l;
                }
                else {
                    while (i < l && this.text.charAt(i) == ' ') {
                        ++i;
                    }
                }
            }
            else {
                while (i > 0 && this.text.charAt(i - 1) == ' ') {
                    --i;
                }
                while (i > 0 && this.text.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }
        return i;
    }
    
    public void moveCursorBy(final int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }
    
    public void setCursorPosition(final int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        final int i = this.text.length();
        this.setSelectionPos(this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i));
    }
    
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }
    
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }
    
    public boolean keyTyped(final char cha, final int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        this.timerUtil.reset();
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.writeText(GuiScreen.getClipboardString());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            this.writeText("");
            return true;
        }
        switch (keyCode) {
            case 14: {
                if (GuiScreen.isCtrlKeyDown()) {
                    this.deleteWords(-1);
                }
                else {
                    this.deleteFromCursor(-1);
                }
                return true;
            }
            case 199: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                }
                else {
                    this.setCursorPositionZero();
                }
                return true;
            }
            case 203: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    }
                    else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                }
                else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                }
                else {
                    this.moveCursorBy(-1);
                }
                return true;
            }
            case 205: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    }
                    else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                }
                else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                }
                else {
                    this.moveCursorBy(1);
                }
                return true;
            }
            case 207: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                }
                else {
                    this.setCursorPositionEnd();
                }
                return true;
            }
            case 211: {
                if (GuiScreen.isCtrlKeyDown()) {
                    this.deleteWords(1);
                }
                else {
                    this.deleteFromCursor(1);
                }
                return true;
            }
            default: {
                if (ChatAllowedCharacters.isAllowedCharacter(cha)) {
                    this.writeText(Character.toString(cha));
                    return true;
                }
                return false;
            }
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        final boolean flag = HoveringUtil.isHovering(this.xPosition, this.yPosition, this.width, this.height, mouseX, mouseY);
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }
    }
    
    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.isFocused()) {
                Keyboard.enableRepeatEvents(true);
            }
            Color textColorWithAlpha = this.focusedTextColor;
            if (this.textAlpha != 1.0f) {
                textColorWithAlpha = ColorUtil.applyOpacity(this.focusedTextColor, this.textAlpha);
            }
            final float xPos = this.xPosition + 3.0f;
            final float yPos = this.yPosition + this.height;
            if (this.isDrawingBackground()) {
                if (this.outline != null) {
                    RoundedUtils.drawRound(this.xPosition - 1.0f, this.yPosition - 1.0f, this.width + 2.0f, this.height + 2.0f, this.radius + 1.0f, this.outline);
                }
                RoundedUtils.drawRound(this.xPosition, this.yPosition, this.width, this.height, this.radius, ColorUtil.applyOpacity(this.fill, this.alpha));
            }
            this.textColor.setDirection(this.isFocused() ? Direction.BACKWARDS : Direction.FORWARDS);
            if (this.backgroundText != null) {
                final Color backgroundTextColor = ColorUtil.applyOpacity(ColorUtil.applyOpacity(this.unfocusedTextColor, this.textAlpha), this.textColor.getOutput().floatValue());
                if (this.text.equals("") && !this.textColor.finished(Direction.BACKWARDS)) {
                    FontManager.getProductSansRegular(20).drawString(this.backgroundText, xPos, yPos - FontManager.getProductSansRegular(25).height(), backgroundTextColor.getRGB());
                }
            }
            final int cursorPos = this.cursorPosition - this.lineScrollOffset;
            int selEnd = this.selectionEnd - this.lineScrollOffset;
            final String text = this.text.substring(this.lineScrollOffset);
            final boolean cursorInBounds = cursorPos >= 0 && cursorPos <= text.length();
            final boolean canShowCursor = this.isFocused && cursorInBounds;
            float j1 = xPos;
            if (selEnd > text.length()) {
                selEnd = text.length();
            }
            if (text.length() > 0) {
                String s1 = cursorInBounds ? text.substring(0, cursorPos) : text;
                float textWidth = (float)FontManager.getProductSansRegular(20).width(s1);
                if (textWidth > this.width - 6.0f) {
                    int endIndex;
                    for (endIndex = s1.length(); textWidth > this.width - 6.0f && endIndex > 0; textWidth = (float)FontManager.getProductSansRegular(20).width(s1), --endIndex) {
                        s1 = s1.substring(0, endIndex - 1);
                    }
                    if (endIndex > 0) {
                        s1 += "...";
                    }
                }
                j1 = FontManager.getProductSansRegular(20).drawStringWithShadow(s1, xPos, yPos - FontManager.getProductSansRegular(25).height(), textColorWithAlpha.getRGB()) + 0.5f;
            }
            final boolean cursorEndPos = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            float k1 = j1;
            if (!cursorInBounds) {
                k1 = ((cursorPos > 0) ? (xPos + this.width) : xPos);
            }
            else if (cursorEndPos) {
                k1 = j1;
                --j1;
            }
            final boolean cursorBlink = this.timerUtil.hasTimeElapsed(2000L) || cursorEndPos;
            if (canShowCursor) {
                if (cursorBlink) {
                    if (this.cursorBlinkAnimation.isDone()) {
                        this.cursorBlinkAnimation.changeDirection();
                    }
                }
                else {
                    this.cursorBlinkAnimation.setDirection(Direction.FORWARDS);
                }
                Gui.drawRect2(k1 - FontManager.getProductSansRegular(20).width(text) + 4.0f, yPos - FontManager.getProductSansRegular(25).height() - 5.0f, 0.5, FontManager.getProductSansRegular(25).height() + 3.0f, ColorUtil.applyOpacity(textColorWithAlpha, this.cursorBlinkAnimation.getOutput().floatValue()).getRGB());
            }
            if (selEnd != cursorPos) {
                final int l1 = (int)(xPos + FontManager.getProductSansRegular(25).width(text.substring(0, selEnd)));
                final int offset = (selEnd > cursorPos) ? 2 : 0;
                final float widthOffset = (selEnd > cursorPos) ? 0.5f : 0.0f;
                this.drawSelectionBox(k1 + offset - FontManager.getProductSansRegular(25).width(text) + 10.0f, yPos - 3.0f - FontManager.getProductSansRegular(25).height(), l1 + widthOffset, yPos - FontManager.getProductSansRegular(25).height() + 12.0f);
            }
        }
    }
    
    private void drawSelectionBox(float x, float y, float width, float height) {
        if (x < width) {
            final float i = x;
            x = width;
            width = i;
        }
        if (y < height) {
            final float j = y;
            y = height;
            height = j;
        }
        if (width > this.xPosition + this.width) {
            width = this.xPosition + this.width;
        }
        if (x > this.xPosition + this.width) {
            x = this.xPosition + this.width;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, height, 0.0).endVertex();
        worldrenderer.pos(width, height, 0.0).endVertex();
        worldrenderer.pos(width, y, 0.0).endVertex();
        worldrenderer.pos(x, y, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
    
    public void setMaxStringLength(final int len) {
        this.maxStringLength = len;
        if (this.text.length() > len) {
            this.text = this.text.substring(0, len);
        }
    }
    
    public int getMaxStringLength() {
        return this.maxStringLength;
    }
    
    public int getCursorPosition() {
        return this.cursorPosition;
    }
    
    public void setTextColor(final Color color) {
        this.focusedTextColor = color;
    }
    
    public void setDisabledTextColour(final Color color) {
        this.unfocusedTextColor = color;
    }
    
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
    
    public float getWidth() {
        final boolean flag = this.backgroundText != null && this.backgroundText.equals("Search");
        return this.isDrawingBackground() ? (this.width - (flag ? 17 : 4)) : this.width;
    }
    
    public float getRealWidth() {
        return this.isDrawingBackground() ? (this.width - 4.0f) : this.width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setSelectionPos(int selectionPos) {
        final int i = this.text.length();
        if (selectionPos > i) {
            selectionPos = i;
        }
        if (selectionPos < 0) {
            selectionPos = 0;
        }
        this.selectionEnd = selectionPos;
        if (this.font != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }
            final float j = this.getWidth();
            final String s = this.text.substring(this.lineScrollOffset);
            final int k = s.length() + this.lineScrollOffset;
            if (selectionPos == this.lineScrollOffset) {
                this.lineScrollOffset -= FontManager.getProductSansRegular(25).width(this.text);
            }
            if (selectionPos > k) {
                this.lineScrollOffset += selectionPos - k;
            }
            else if (selectionPos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - selectionPos;
            }
            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }
    
    public void setCanLoseFocus(final boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }
    
    public boolean getVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public void setFont(final FontManager font) {
        this.font = font;
    }
    
    public float getXPosition() {
        return this.xPosition;
    }
    
    public float getYPosition() {
        return this.yPosition;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public float getAlpha() {
        return this.alpha;
    }
    
    public void setXPosition(final float xPosition) {
        this.xPosition = xPosition;
    }
    
    public void setYPosition(final float yPosition) {
        this.yPosition = yPosition;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public void setAlpha(final float alpha) {
        this.alpha = alpha;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public void setTextAlpha(final float textAlpha) {
        this.textAlpha = textAlpha;
    }
    
    public void setOutline(final Color outline) {
        this.outline = outline;
    }
    
    public void setFill(final Color fill) {
        this.fill = fill;
    }
    
    public Color getOutline() {
        return this.outline;
    }
    
    public Color getFill() {
        return this.fill;
    }
    
    public void setBackgroundText(final String backgroundText) {
        this.backgroundText = backgroundText;
    }
    
    public void setDrawingBackground(final boolean drawingBackground) {
        this.drawingBackground = drawingBackground;
    }
    
    public boolean isDrawingBackground() {
        return this.drawingBackground;
    }
    
    public void setFocused(final boolean isFocused) {
        this.isFocused = isFocused;
    }
    
    public boolean isFocused() {
        return this.isFocused;
    }
}
