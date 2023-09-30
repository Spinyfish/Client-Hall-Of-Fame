package club.pulsive.impl.util.render.secondary;


import club.pulsive.altmanager.GuiAltManager;

import club.pulsive.altmanager.GuiAltManagerNew;
import club.pulsive.api.font.Fonts;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.render.RoundedUtil;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;


public class GuiTextFieldCustom extends Gui
{

    private final int id;
    private final FontRenderer fontRendererInstance;
    /**
     * The width of this text field.
     */
    private final int width;
    private final int height;
    public int xPosition;
    public int yPosition;
    /**
     * Has the current text being edited on the textbox.
     */
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;

    /**
     * If this value is true along with isEnabled, keyTyped will process the keys.
     */
    private boolean isFocused;

    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    private boolean isEnabled = true;

    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int lineScrollOffset;
    private int cursorPosition;

    /**
     * other selection position, maybe the same as the cursor
     */
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;

    /**
     * True if this textbox is visible
     */
    private boolean visible = true;
    private GuiPageButtonList.GuiResponder field_175210_x;
    private Predicate<String> field_175209_y = Predicates.alwaysTrue();

    public GuiTextFieldCustom(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        id = componentId;
        fontRendererInstance = fontrendererObj;
        xPosition = x;
        yPosition = y;
        width = par5Width;
        height = par6Height;
    }

    public void func_175207_a(GuiPageButtonList.GuiResponder p_175207_1_) {
        field_175210_x = p_175207_1_;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter() {
        ++cursorCounter;
    }

    /**
     * Returns the contents of the textbox
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText(String p_146180_1_) {
        if (field_175209_y.apply(p_146180_1_)) {
            if (p_146180_1_.length() > maxStringLength) {
                text = p_146180_1_.substring(0, maxStringLength);
            } else {
                text = p_146180_1_;
            }

            setCursorPositionEnd();
        }
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText() {
        int i = ApacheMath.min(cursorPosition, selectionEnd);
        int j = ApacheMath.max(cursorPosition, selectionEnd);
        return text.substring(i, j);
    }

    public void func_175205_a(Predicate<String> p_175205_1_) {
        field_175209_y = p_175205_1_;
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText(String p_146191_1_) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        int i = ApacheMath.min(cursorPosition, selectionEnd);
        int j = ApacheMath.max(cursorPosition, selectionEnd);
        int k = maxStringLength - text.length() - (i - j);
        int l = 0;

        if (text.length() > 0) {
            s = s + text.substring(0, i);
        }

        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (text.length() > 0 && j < text.length()) {
            s = s + text.substring(j);
        }

        if (field_175209_y.apply(s)) {
            text = s;
            moveCursorBy(i - selectionEnd + l);

            if (field_175210_x != null) {
                field_175210_x.func_175319_a(id, text);
            }
        }
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(int p_146177_1_) {
        if (text.length() != 0) {
            if (selectionEnd != cursorPosition) {
                writeText("");
            } else {
                deleteFromCursor(getNthWordFromCursor(p_146177_1_) - cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int p_146175_1_) {
        if (text.length() != 0) {
            if (selectionEnd != cursorPosition) {
                writeText("");
            } else {
                boolean flag = p_146175_1_ < 0;
                int i = flag ? cursorPosition + p_146175_1_ : cursorPosition;
                int j = flag ? cursorPosition : cursorPosition + p_146175_1_;
                String s = "";

                if (i >= 0) {
                    s = text.substring(0, i);
                }

                if (j < text.length()) {
                    s = s + text.substring(j);
                }

                if (field_175209_y.apply(s)) {
                    text = s;

                    if (flag) {
                        moveCursorBy(p_146175_1_);
                    }

                    if (field_175210_x != null) {
                        field_175210_x.func_175319_a(id, text);
                    }
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(int p_146187_1_) {
        return getNthWordFromPos(p_146187_1_, getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int p_146183_1_, int p_146183_2_) {
        return func_146197_a(p_146183_1_, p_146183_2_, true);
    }

    public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        int i = p_146197_2_;
        boolean flag = p_146197_1_ < 0;
        int j = ApacheMath.abs(p_146197_1_);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = text.length();
                i = text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (p_146197_3_ && i < l && text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (p_146197_3_ && i > 0 && text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && text.charAt(i - 1) != 32) {
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
        setCursorPosition(selectionEnd + p_146182_1_);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero() {
        setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd() {
        setCursorPosition(text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        if (!isFocused) {
            return false;
        } else if (GuiScreen.isKeyComboCtrlA(p_146201_2_)) {
            setCursorPositionEnd();
            setSelectionPos(0);
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(p_146201_2_)) {
            GuiScreen.setClipboardString(getSelectedText());
            return true;
        } else if (GuiScreen.isKeyComboCtrlV(p_146201_2_)) {
            if (isEnabled) {
                writeText(GuiScreen.getClipboardString());
            }

            return true;
        } else if (GuiScreen.isKeyComboCtrlX(p_146201_2_)) {
            GuiScreen.setClipboardString(getSelectedText());

            if (isEnabled) {
                writeText("");
            }

            return true;
        } else {
            switch (p_146201_2_) {
                case 14:
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (isEnabled) {
                            deleteWords(-1);
                        }
                    } else if (isEnabled) {
                        deleteFromCursor(-1);
                    }

                    return true;

                case 199:
                    if (GuiScreen.isShiftKeyDown()) {
                        setSelectionPos(0);
                    } else {
                        setCursorPositionZero();
                    }

                    return true;

                case 203:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(-1, getSelectionEnd()));
                        } else {
                            setSelectionPos(getSelectionEnd() - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        setCursorPosition(getNthWordFromCursor(-1));
                    } else {
                        moveCursorBy(-1);
                    }

                    return true;

                case 205:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(1, getSelectionEnd()));
                        } else {
                            setSelectionPos(getSelectionEnd() + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        setCursorPosition(getNthWordFromCursor(1));
                    } else {
                        moveCursorBy(1);
                    }

                    return true;

                case 207:
                    if (GuiScreen.isShiftKeyDown()) {
                        setSelectionPos(text.length());
                    } else {
                        setCursorPositionEnd();
                    }

                    return true;

                case 211:
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (isEnabled) {
                            deleteWords(1);
                        }
                    } else if (isEnabled) {
                        deleteFromCursor(1);
                    }

                    return true;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_)) {
                        if (isEnabled) {
                            writeText(Character.toString(p_146201_1_));
                        }

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
    public void mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_) {
        boolean flag = p_146192_1_ >= xPosition && p_146192_1_ < xPosition + width && p_146192_2_ >= yPosition && p_146192_2_ < yPosition + height;

        if (canLoseFocus) {
            setFocused(flag);
        }

        if (isFocused && flag && p_146192_3_ == 0) {
            int i = p_146192_1_ - xPosition;

            if (enableBackgroundDrawing) {
                i -= 4;
            }

            String s = fontRendererInstance.trimStringToWidth(text.substring(lineScrollOffset), getWidth());
            setCursorPosition(fontRendererInstance.trimStringToWidth(s, i).length() + lineScrollOffset);
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox() {
        if (getVisible()) {
            if (getEnableBackgroundDrawing()) {
                if(Minecraft.getMinecraft().currentScreen instanceof GuiAltManagerNew)  {
                    RoundedUtil.drawRoundedOutline(xPosition, yPosition, xPosition + width, yPosition + height, 8, 3,-1);
                } else {
                    drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFF25252E);
                }


            }

            int i = isEnabled ? enabledColor : disabledColor;
            int j = cursorPosition - lineScrollOffset;
            int k = selectionEnd - lineScrollOffset;
            String s = fontRendererInstance.trimStringToWidth(text.substring(lineScrollOffset), getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = isFocused && cursorCounter / 6 % 2 == 0 && flag;
            int l = enableBackgroundDrawing ? xPosition + 4 : xPosition;
            int i1 = enableBackgroundDrawing ? yPosition + (height - 8) / 2 : yPosition;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = (int) Fonts.popMed20.drawStringWithShadow(s1, (float) l, (float) i1 - 5, i);
            }

            boolean flag2 = cursorPosition < text.length() || text.length() >= getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length()) {
                j1 = (int) Fonts.popMed20.drawStringWithShadow(s.substring(j), (float) j1, (float) i1 -5 , i);
            }

            if (flag1) {
                if (flag2) {
                    Fonts.moon.drawStringWithShadow("|", (float) k1 - 2, (float) i1, -3092272);
                } else {
                    Fonts.moon.drawStringWithShadow("|", (float) k1 - 1, (float) i1, i);
                }
            }

            if (k != j) {
                int l1 = l + Fonts.popMed20.getStringWidth(s.substring(0, k));
                drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + fontRendererInstance.FONT_HEIGHT);
            }
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        if (p_146188_1_ < p_146188_3_) {
            int i = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = i;
        }

        if (p_146188_2_ < p_146188_4_) {
            int j = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = j;
        }

        if (p_146188_3_ > xPosition + width) {
            p_146188_3_ = xPosition + width;
        }

        if (p_146188_1_ > xPosition + width) {
            p_146188_1_ = xPosition + width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_146188_1_, p_146188_4_, 0.0D).endVertex();
        worldrenderer.pos(p_146188_3_, p_146188_4_, 0.0D).endVertex();
        worldrenderer.pos(p_146188_3_, p_146188_2_, 0.0D).endVertex();
        worldrenderer.pos(p_146188_1_, p_146188_2_, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength() {
        return maxStringLength;
    }

    public void setMaxStringLength(int p_146203_1_) {
        maxStringLength = p_146203_1_;

        if (text.length() > p_146203_1_) {
            text = text.substring(0, p_146203_1_);
        }
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return cursorPosition;
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(int p_146190_1_) {
        cursorPosition = p_146190_1_;
        int i = text.length();
        cursorPosition = MathHelper.clamp_int(cursorPosition, 0, i);
        setSelectionPos(cursorPosition);
    }

    /**
     * get enable drawing background and outline
     */
    public boolean getEnableBackgroundDrawing() {
        return enableBackgroundDrawing;
    }

    /**
     * enable drawing background and outline
     */
    public void setEnableBackgroundDrawing(boolean p_146185_1_) {
        enableBackgroundDrawing = p_146185_1_;
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor(int p_146193_1_) {
        enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour(int p_146204_1_) {
        disabledColor = p_146204_1_;
    }

    /**
     * Getter for the focused field
     */
    public boolean isFocused() {
        return isFocused;
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocused(boolean p_146195_1_) {
        if (p_146195_1_ && !isFocused) {
            cursorCounter = 0;
        }

        isFocused = p_146195_1_;
    }

    public void setEnabled(boolean p_146184_1_) {
        isEnabled = p_146184_1_;
    }

    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    public int getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWidth() {
        return getEnableBackgroundDrawing() ? width - 8 : width;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int p_146199_1_) {
        int i = text.length();

        if (p_146199_1_ > i) {
            p_146199_1_ = i;
        }

        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }

        selectionEnd = p_146199_1_;

        if (fontRendererInstance != null) {
            if (lineScrollOffset > i) {
                lineScrollOffset = i;
            }

            int j = getWidth();
            String s = fontRendererInstance.trimStringToWidth(text.substring(lineScrollOffset), j);
            int k = s.length() + lineScrollOffset;

            if (p_146199_1_ == lineScrollOffset) {
                lineScrollOffset -= fontRendererInstance.trimStringToWidth(text, j, true).length();
            }

            if (p_146199_1_ > k) {
                lineScrollOffset += p_146199_1_ - k;
            } else if (p_146199_1_ <= lineScrollOffset) {
                lineScrollOffset -= lineScrollOffset - p_146199_1_;
            }

            lineScrollOffset = MathHelper.clamp_int(lineScrollOffset, 0, i);
        }
    }

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    public void setCanLoseFocus(boolean p_146205_1_) {
        canLoseFocus = p_146205_1_;
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible(boolean p_146189_1_) {
        visible = p_146189_1_;
    }
}