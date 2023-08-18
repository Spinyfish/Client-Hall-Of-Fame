package me.jinthium.straight.api.clickgui.dropdown.components.settings;

import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.font.CustomFont;
import me.jinthium.straight.impl.utils.font.FontUtil;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjglx.input.Keyboard;

import java.awt.*;

public class TextField extends Gui {

    public CustomFont font;

    private float xPosition, yPosition, radius = 2, alpha = 1;

    private float width, height, textAlpha = 1;

    private Color outline = Color.WHITE, fill = ColorUtil.tripleColor(32);

    private Color focusedTextColor = new Color(224, 224, 224);
    private Color unfocusedTextColor = new Color(130, 130, 130);
    /**
     * Has the current text being edited on the textbox.
     */
    private String text = "";

    private String backgroundText;

    private int maxStringLength = 32;

    private boolean drawingBackground = true;

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;

    /**
     * If this value is true along with isEnabled, keyTyped will process the keys.
     */

    private boolean isFocused;

    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int lineScrollOffset;
    private int cursorPosition;


    /**
     * other selection position, maybe the same as the cursor
     */
    private int selectionEnd;
    private final Animation textColor = new DecelerateAnimation(250, 1);
    private final Animation cursorBlinkAnimation = new DecelerateAnimation(750, 1);
    private final TimerUtil timerUtil = new TimerUtil();

    /**
     * True if this textbox is visible
     */
    private boolean visible = true;

    public TextField(CustomFont font) {
        this.font = font;
    }

    public TextField(CustomFont font, float x, float y, float par5Width, float par6Height) {
        this.font = font;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText(String text) {
        if (text.length() > this.maxStringLength) {
            this.text = text.substring(0, this.maxStringLength);
        } else {
            this.text = text;
        }

        setCursorPositionZero();
    }




    /**
     * Returns the contents of the textbox
     */
    public String getText() {
        return this.text;
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText(String text) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(text);
        int min = Math.min(this.cursorPosition, this.selectionEnd);
        int max = Math.max(this.cursorPosition, this.selectionEnd);
        int len = this.maxStringLength - this.text.length() - (min - max);
        int l;

        if (this.text.length() > 0) {
            s = s + this.text.substring(0, min);
        }

        if (len < s1.length()) {
            s = s + s1.substring(0, len);
            l = len;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (this.text.length() > 0 && max < this.text.length()) {
            s = s + this.text.substring(max);
        }

        this.text = s;
        this.moveCursorBy(min - this.selectionEnd + l);


    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean negative = num < 0;
                int i = negative ? this.cursorPosition + num : this.cursorPosition;
                int j = negative ? this.cursorPosition : this.cursorPosition + num;
                String s = "";

                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                this.text = s;

                if (negative) {
                    this.moveCursorBy(num);
                }

            }
        }
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int n, int pos) {
        return this.func_146197_a(n, pos);
    }

    public int func_146197_a(int n, int pos) {
        int i = pos;
        boolean negative = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k) {
            if (!negative) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean keyTyped(char cha, int keyCode) {
        if (!this.isFocused) {
            return false;
        }

        timerUtil.reset();

        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.writeText(GuiScreen.getClipboardString());


            return true;
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            this.writeText("");


            return true;
        } else {
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(-1);
                    } else {
                        this.deleteFromCursor(-1);
                    }

                    return true;

                case Keyboard.KEY_HOME:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }

                    return true;

                case Keyboard.KEY_LEFT:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }

                    return true;

                case Keyboard.KEY_RIGHT:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }

                    return true;

                case Keyboard.KEY_END:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }

                    return true;

                case Keyboard.KEY_DELETE:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(1);
                    } else {
                        this.deleteFromCursor(1);
                    }

                    return true;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(cha)) {
                        this.writeText(Character.toString(cha));

                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = HoveringUtil.isHovering(xPosition, yPosition, width, height, mouseX, mouseY);

        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused && flag && mouseButton == 0) {
            float xPos = xPosition;
            if (backgroundText != null && backgroundText.equals("Search")) {
                xPos += 13;
            }

            float i = mouseX - xPos;

            String s = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int) this.getWidth());
            this.setCursorPosition(this.font.trimStringToWidth(s, (int) i).length() + this.lineScrollOffset);
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox() {
        if (this.getVisible()) {

            if (isFocused()) {
                Keyboard.enableRepeatEvents(true);
            }

            Color textColorWithAlpha = focusedTextColor;
            if (textAlpha != 1) {
                textColorWithAlpha = ColorUtil.applyOpacity(focusedTextColor, textAlpha);
            }


            float xPos = this.xPosition + 3;
            float yPos = this.yPosition + font.getMiddleOfBox(height);

            if (this.isDrawingBackground()) {
                if (outline != null) {
                    RoundedUtil.drawRound(this.xPosition - 1, this.yPosition - 1, this.width + 2f, this.height + 2f,
                            radius + 1, outline);
                }

                RoundedUtil.drawRound(this.xPosition, this.yPosition, this.width, this.height, radius, ColorUtil.applyOpacity(fill, alpha));
            } else {
                float rectHeight = 1;
                Gui.drawRect2(xPosition, yPosition + height - rectHeight, width, rectHeight,
                        ColorUtil.interpolateColor(focusedTextColor, unfocusedTextColor, textColor.getOutput().floatValue()));
            }

            textColor.setDirection(isFocused() ? Direction.BACKWARDS : Direction.FORWARDS);
            if (backgroundText != null) {
                Color backgroundTextColor = ColorUtil.applyOpacity(ColorUtil.applyOpacity(unfocusedTextColor, textAlpha), textColor.getOutput().floatValue());
                if (backgroundText.equals("Search")) {
                    Util.iconFont16.drawString(FontUtil.SEARCH, xPos + 1.5f, this.yPosition + Util.iconFont16.getMiddleOfBox(getHeight()), ColorUtil.applyOpacity(unfocusedTextColor, textAlpha));
                    xPos += 15;
                }

                if (text.equals("") && !textColor.finished(Direction.BACKWARDS)) {
                    font.drawString(backgroundText, xPos, yPos + 2, backgroundTextColor);
                }
            }


            int cursorPos = this.cursorPosition - this.lineScrollOffset;
            int selEnd = this.selectionEnd - this.lineScrollOffset;
            String text = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int) this.getWidth());
            boolean cursorInBounds = cursorPos >= 0 && cursorPos <= text.length();
            boolean canShowCursor = this.isFocused && cursorInBounds;
            float j1 = xPos;


            if (selEnd > text.length()) {
                selEnd = text.length();
            }


            if (text.length() > 0) {
                String s1 = cursorInBounds ? text.substring(0, cursorPos) : text;
                j1 = this.font.drawStringWithShadow(s1, xPos, yPos + 2, textColorWithAlpha.getRGB()) + .5f;
            }

            boolean cursorEndPos = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            float k1 = j1;

            if (!cursorInBounds) {
                k1 = cursorPos > 0 ? xPos + this.width : xPos;
            } else if (cursorEndPos) {
                k1 = j1;
                --j1;
            }

            if (text.length() > 0 && cursorInBounds && cursorPos < text.length()) {
                j1 = this.font.drawStringWithShadow(text.substring(cursorPos), j1 + 2f, yPos + 2, textColorWithAlpha.getRGB());
            }

            boolean cursorBlink = timerUtil.hasTimeElapsed(2000) || cursorEndPos;

            if (canShowCursor) {
                if (cursorBlink) {
                    if (cursorBlinkAnimation.isDone()) {
                        cursorBlinkAnimation.changeDirection();
                    }
                } else cursorBlinkAnimation.setDirection(Direction.FORWARDS);

                Gui.drawRect2(k1 + 1, yPos - 2, .5f, font.getHeight() + 3,
                        ColorUtil.applyOpacity(textColorWithAlpha, cursorBlinkAnimation.getOutput().floatValue()).getRGB());
            }

            if (selEnd != cursorPos) {
                int l1 = (int) (xPos + this.font.getStringWidth(text.substring(0, selEnd)));
                int offset = selEnd > cursorPos ? 2 : 0;
                float widthOffset = selEnd > cursorPos ? .5f : 0;

                drawSelectionBox(k1 + offset, yPos - 1, l1 + widthOffset, yPos + 1 + font.getHeight());
            }
        }

    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawSelectionBox(float x, float y, float width, float height) {
        if (x < width) {
            float i = x;
            x = width;
            width = i;
        }

        if (y < height) {
            float j = y;
            y = height;
            height = j;
        }

        if (width > this.xPosition + this.width) {
            width = this.xPosition + this.width;
        }

        if (x > this.xPosition + this.width) {
            x = this.xPosition + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, height, 0.0D).endVertex();
        worldrenderer.pos(width, height, 0.0D).endVertex();
        worldrenderer.pos(width, y, 0.0D).endVertex();
        worldrenderer.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int len) {
        this.maxStringLength = len;

        if (this.text.length() > len) {
            this.text = this.text.substring(0, len);
        }
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return this.cursorPosition;
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor(Color color) {
        this.focusedTextColor = color;
    }

    public void setDisabledTextColour(Color color) {
        this.unfocusedTextColor = color;
    }

    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public float getWidth() {
        boolean flag = backgroundText != null && backgroundText.equals("Search");
        return this.isDrawingBackground() ? this.width - (flag ? 17 : 4) : this.width;
    }

    public float getRealWidth() {
        return this.isDrawingBackground() ? this.width - 4 : this.width;
    }

    public float getHeight() {
        return this.height;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int selectionPos) {
        int i = this.text.length();

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

            float j = this.getWidth();
            String s = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int) j);
            int k = s.length() + this.lineScrollOffset;

            if (selectionPos == this.lineScrollOffset) {
                this.lineScrollOffset -= this.font.trimStringToWidth(this.text, (int) j, true).length();
            }

            if (selectionPos > k) {
                this.lineScrollOffset += selectionPos - k;
            } else if (selectionPos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - selectionPos;
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible() {
        return this.visible;
    }

    /**
     * Sets whether this textbox is visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public CustomFont getFont() {
        return font;
    }

    public void setFont(CustomFont font) {
        this.font = font;
    }

    public float getXPosition() {
        return xPosition;
    }

    public void setXPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(float textAlpha) {
        this.textAlpha = textAlpha;
    }

    public Color getOutline() {
        return outline;
    }

    public void setOutline(Color outline) {
        this.outline = outline;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public Color getFocusedTextColor() {
        return focusedTextColor;
    }

    public void setFocusedTextColor(Color focusedTextColor) {
        this.focusedTextColor = focusedTextColor;
    }

    public Color getUnfocusedTextColor() {
        return unfocusedTextColor;
    }

    public void setUnfocusedTextColor(Color unfocusedTextColor) {
        this.unfocusedTextColor = unfocusedTextColor;
    }

    public String getBackgroundText() {
        return backgroundText;
    }

    public void setBackgroundText(String backgroundText) {
        this.backgroundText = backgroundText;
    }

    public boolean isDrawingBackground() {
        return drawingBackground;
    }

    public void setDrawingBackground(boolean drawingBackground) {
        this.drawingBackground = drawingBackground;
    }

    public boolean isCanLoseFocus() {
        return canLoseFocus;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public int getLineScrollOffset() {
        return lineScrollOffset;
    }

    public void setLineScrollOffset(int lineScrollOffset) {
        this.lineScrollOffset = lineScrollOffset;
    }

    public void setSelectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
    }

    public Animation getTextColor() {
        return textColor;
    }

    public Animation getCursorBlinkAnimation() {
        return cursorBlinkAnimation;
    }

    public TimerUtil getTimerUtil() {
        return timerUtil;
    }

    public boolean isVisible() {
        return visible;
    }


}