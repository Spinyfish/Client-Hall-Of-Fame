package com.alan.clients.util.gui.textbox;

import com.alan.clients.util.font.Font;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.vector.Vector2d;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

@Getter
@Setter
public class TextBox {
    public String text = "";
    public Vector2d position;
    public boolean selected;
    public int cursor;
    public double animatedCursorPosition;
    public Font fontRenderer;
    public Color color;
    private double lastTime;
    private double lastBackSpace;
    private TextAlign textAlign;
    private double posX;
    private String emptyText;
    private float width;
    private boolean hideCharacters;

    public TextBox(final Vector2d position, final Font fontRenderer, final Color color, final TextAlign textAlign, final String emptyText, final float width, final boolean hideCharacters) {
        this.position = position;
        this.fontRenderer = fontRenderer;
        this.color = color;
        this.textAlign = textAlign;
        this.emptyText = emptyText;
        this.width = width;
        this.hideCharacters = hideCharacters;
    }

    public TextBox(final Vector2d position, final Font fontRenderer, final Color color, final TextAlign textAlign, final String emptyText, final float width) {
        this.position = position;
        this.fontRenderer = fontRenderer;
        this.color = color;
        this.textAlign = textAlign;
        this.emptyText = emptyText;
        this.width = width;
        this.hideCharacters = false;
    }

    public void draw() {
        /* Makes sure the cursor doesn't go out of bounds */
        cursor = Math.min(Math.max(cursor, 0), this.text.length());

        Keyboard.enableRepeatEvents(true);

        StringBuilder drawnString = new StringBuilder(this.text);

        if (this.hideCharacters && !this.isEmpty()) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < drawnString.length(); i++) {
                string.append("*");
            }
            drawnString = new StringBuilder(string);
        }

        /* Used for animations */
        final double time = System.currentTimeMillis();
        double difference = Math.abs(time - lastTime);
        lastTime = time;
        difference = Math.min(difference, 500);

        /* Allows text aligning to work */
        switch (this.textAlign) {
            case CENTER:
                final int speed = 20;
                for (int i = 0; i < difference; ++i) {
                    posX = ((posX * (speed - 1)) + (position.x - this.fontRenderer.width(this.text.isEmpty() ||
                            this.text.equals(" ") ? this.emptyText : drawnString.toString()) / 2f)) / speed;
                }
                break;

            case LEFT:
                posX = position.x;
                break;
        }

        /* Renders Text */
        if (this.isEmpty()) {
            this.fontRenderer.drawString(this.emptyText, posX, position.y, new Color(this.color.getRed(),
                    this.color.getBlue(), this.color.getGreen(), (int) (this.color.getAlpha() * (this.selected ? 0.3F : 0.2F))).hashCode());
        } else {
            this.fontRenderer.drawString(drawnString.toString(), posX, position.y, this.color.hashCode());
        }

        /* Makes sure the cursor doesn't go out of bounds */
        cursor = Math.min(Math.max(cursor, 0), drawnString.length());

        /* Renders cursor */
        final StringBuilder textBeforeCursor = new StringBuilder();
        for (int i = 0; i < this.cursor; ++i) {
            textBeforeCursor.append(drawnString.charAt(i));
        }

        final float cursorOffset = this.fontRenderer.width(textBeforeCursor.toString());

        /* Allows for cursor animations */
        final int speed = 20;
        for (int i = 0; i < difference; ++i) {
            animatedCursorPosition = (animatedCursorPosition * (speed - 1) + (cursorOffset - 2)) / speed;
        }

        /* Renders cursor */
        if (this.selected) {
            this.fontRenderer.drawString("|", (float) (posX + animatedCursorPosition + 1), position.y - 1, new Color(this.color.getRed(), this.color.getBlue(), this.color.getGreen(), this.color.getAlpha() == 0 ? 0 : (int) ((((Math.sin(System.currentTimeMillis() / 150D) + 1) / 2) * 255))).hashCode());
        }
    }

    public void click(final int mouseX, final int mouseY, final int mouseButton) {
        final Vector2d position = getPosition();

        this.selected = mouseButton == 0 && GUIUtil.mouseOver(position.x + (textAlign == TextAlign.CENTER ? -width / 2f : 0),
                position.y, width, fontRenderer.height(), mouseX, mouseY);
    }

    public void key(final char typedChar, final int keyCode) {
        if (!this.selected) {
            return;
        }

        final String character = String.valueOf(typedChar);

        /* Makes sure the cursor doesn't go out of bounds */
        cursor = Math.min(Math.max(cursor, 0), this.text.length());

        /* Allows ctrl v to work */
        if (Keyboard.isKeyDown(29) && keyCode == 47) {

            try {
                final String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                this.addText(clipboard, cursor);
                cursor += clipboard.length();
            } catch (final UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }

            /* Allows you to type letters and numbers */
        } else if ("abcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()-_+=[{]};:.>,</?| ".contains(character.toLowerCase())) {

            this.addText(character, cursor);
            cursor++;

            /* Allows for the delete button to delete the text in front of the cursor */
        } else if (keyCode == 211 && !this.text.isEmpty()) {
            this.removeText(cursor);
        } else if (keyCode == 14 && !this.text.isEmpty()) {

            /* Normal backspacing */
            this.removeText(cursor);
            cursor--;

            /* Makes ctrl backspace work */
            if (Keyboard.isKeyDown(29)) {
                while (!this.text.isEmpty() && 0 < cursor) {
                    this.removeText(cursor);
                    cursor--;
                }
            }

            /* Allows for the arrow keys to be used to move the cursor */
        } else if (keyCode == 205) {
            cursor++;

            if (Keyboard.isKeyDown(29)) {
                while (this.text.length() > cursor) {
                    cursor++;
                }
            }
        } else if (keyCode == 203) {
            cursor--;

            if (Keyboard.isKeyDown(29)) {
                while (0 < cursor) {
                    cursor--;
                }
            }
        }

        /* Makes sure the cursor doesn't go out of bounds */
        cursor = Math.min(Math.max(cursor, 0), this.text.length());
    }

    private void addText(final String text, final int position) {
        if (fontRenderer.width(this.text + text) <= width) {
            final StringBuilder newText = new StringBuilder();

            boolean append = false;
            for (int i = 0; i < this.text.length(); i++) {
                final String character = String.valueOf(this.text.charAt(i));

                if (i == position) {
                    append = true;
                    newText.append(text);
                }

                newText.append(character);
            }

            if (!append) {
                newText.append(text);
            }

            this.text = newText.toString();
        }
    }

    private void removeText(final int position) {
        final StringBuilder newText = new StringBuilder();
        for (int i = 0; i < this.text.length(); ++i) {
            final String character = String.valueOf(this.text.charAt(i));

            if (i != position - 1) {
                newText.append(character);
            }
        }

        this.text = newText.toString();
    }

    public boolean isEmpty() {
        return this.text.isEmpty() || this.text.equals(" ");
    }
}