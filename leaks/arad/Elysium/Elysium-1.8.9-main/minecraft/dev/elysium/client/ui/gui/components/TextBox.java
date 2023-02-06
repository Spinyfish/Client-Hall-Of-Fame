package dev.elysium.client.ui.gui.components;

import dev.elysium.client.Elysium;
import dev.elysium.client.ui.font.TTFFontRenderer;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class TextBox {
    String text = "";
    public String defaultText;
    public int x;
    public int y;
    public TTFFontRenderer fr;
    public int maxX;
    public int maxY;
    public int color;
    public int defaultColor;

    public boolean selected = false;

    public int cursorPos = 0;

    public boolean focused;

    private boolean drawCursor = true;

    public TextBox(String font, String defaultText, int x, int y, int maxX) {
        super();
        this.defaultText = defaultText;
        this.x = x;
        this.y = y;
        this.fr = Elysium.getInstance().fm.getFont(font.toUpperCase());
        this.maxX = maxX;
        this.color = -1;
        this.defaultColor = 0xff898b8c;
        this.maxY = Math.round(Elysium.getInstance().fm.getFont(font.toUpperCase()).getHeight("|")) + 4;
    }

    public TextBox(String font, String defaultText, int x, int y, int maxX, int maxY) {
        super();
        this.defaultText = defaultText;
        this.x = x;
        this.y = y;
        this.fr = Elysium.getInstance().fm.getFont(font.toUpperCase());
        this.maxX = maxX;
        this.maxY = maxY;
        this.color = -1;
        this.defaultColor = 0xff505050;
    }

    public TextBox(String font, String defaultText, int x, int y, int maxX, int color, int defaultColor) {
        super();
        this.defaultText = defaultText;
        this.x = x;
        this.y = y;
        this.fr = Elysium.getInstance().fm.getFont(font.toUpperCase());
        this.maxX = maxX;
        this.color = color;
        this.defaultColor = defaultColor;
        this.text = "";
        this.maxY = Math.round(Elysium.getInstance().fm.getFont(font.toUpperCase()).getHeight("|")) + 4;
    }

    public TextBox(String font, String defaultText, int x, int y, int maxX, int maxY, int color, int defaultColor) {
        super();
        this.defaultText = defaultText;
        this.x = x;
        this.y = y;
        this.fr = Elysium.getInstance().fm.getFont(font.toUpperCase());
        this.maxX = maxX;
        this.color = color;
        this.defaultColor = defaultColor;
        this.text = "";
        this.maxY = maxY;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String s) {
        this.text = s;
    }

    public void clearText() {
        this.text = "";
    }

    public void debugDraw() {
        Gui.drawRect(x - 5, y - 5, x + maxX + 5, y + maxY + 5, 0xaaffffff);
        Gui.drawRect(x, y, x + maxX, y + maxY, 0xaaffffff);
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if((mouseX > x - 5 && mouseX < x + maxX + 5) && (mouseY > y - 5 && mouseY < y + maxY + 5)) {
            this.focused = true;
        } else {
            this.focused = false;
        }
    }

    public void drawText() {
        if(text.isEmpty()) {
            this.selected = false;
            if(this.drawCursor && this.focused) {
                if(System.currentTimeMillis() % 1000 > 500)
                    fr.drawString(text, x, y, color);
                else
                    fr.drawString(text + "_", x, y, color);
            } else {
                fr.drawString(defaultText, x, y, defaultColor);
            }

        } else {
            if(this.selected) {
                Gui.drawRect(this.x, this.y, this.x + this.fr.getStringWidth(this.text) + (this.drawCursor ? 3 : -2), this.y + this.fr.getHeight("|"), 0xff5555ff);
            }

            if(this.drawCursor && this.focused) {
                if(System.currentTimeMillis() % 1000 > 500)
                    fr.drawString(text, x, y, color);
                else
                    fr.drawString(text + "_", x, y, color);

            } else
                fr.drawString(text, x, y, color);
        }
    }

    String[] alphabet = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "k", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "|", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "[", "]", "-", "_", "{", "}", ".", ",", "/", "+", "=", "`", "'","_", "\"", ":", ";", "!", "@", " "};

    public void key(int key) {
        if(!this.focused)
            return;

        if(key == Keyboard.KEY_BACK) {
            if(this.selected) {
                text = ""; return;
            }

            if(text.length() > 0)
                text = text.substring(0, text.length() - 1);
        }
        if(key == Keyboard.KEY_A && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            selected = true;
        }

        if(key == Keyboard.KEY_V && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            if(this.selected)
                text = "";

            addWithMaxX(GuiScreen.getClipboardString());
        }

        if(key == Keyboard.KEY_C && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && this.selected) {
            GuiScreen.setClipboardString(this.text);
        }

        if(key == Keyboard.KEY_RIGHT) {
            cursorPos++;
            if(cursorPos > text.length() - 1)
                cursorPos = text.length() - 1;
        }
        if(key == Keyboard.KEY_LEFT) {
            cursorPos--;
            if(cursorPos < 0)
                cursorPos = 0;
        }
    }

    void addWithMaxX(String text) {
        for(char c : text.toCharArray()) {
            boolean addIt = false;

            for(String s : alphabet) {
                if(String.valueOf(c).equalsIgnoreCase(s)) {
                    addIt = true;
                    break;
                }
            }

            if(this.fr.getStringWidth(this.text + c) > maxX)
                return;

            if(addIt) {
                if(selected)
                    this.text = ""; selected = false;
                this.text += String.valueOf(c); cursorPos++;
            }
        }
    }

    public void type(char c) {
        if(!this.focused)
            return;

        boolean addIt = false;

        for(String s : alphabet) {
            if(String.valueOf(c).equalsIgnoreCase(s)) {
                addIt = true;
                break;
            }
        }

        if(this.fr.getStringWidth(this.text + c) > maxX && !selected)
            return;

        if(addIt) {
            if(selected)
                this.text = ""; selected = false;
            this.text += String.valueOf(c); cursorPos++;
        }
    }
}
