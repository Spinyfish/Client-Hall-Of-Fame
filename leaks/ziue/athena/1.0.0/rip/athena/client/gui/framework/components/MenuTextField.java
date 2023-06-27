package rip.athena.client.gui.framework.components;

import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import rip.athena.client.gui.framework.*;
import org.lwjgl.input.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;

public class MenuTextField extends MenuComponent
{
    protected String text;
    protected int minOffset;
    protected int index;
    protected int lineRefreshTime;
    protected long lineTime;
    protected boolean passwordField;
    protected boolean mouseDown;
    protected boolean focused;
    protected boolean tab;
    protected TextPattern pattern;
    protected ButtonState lastState;
    
    public MenuTextField(final TextPattern pattern, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.minOffset = 2;
        this.index = 0;
        this.lineRefreshTime = 1000;
        this.lineTime = 0L;
        this.passwordField = false;
        this.mouseDown = false;
        this.focused = false;
        this.tab = true;
        this.lastState = ButtonState.NORMAL;
        this.pattern = pattern;
        this.text = "";
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(65, 65, 65, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(75, 75, 75, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(20, 20, 20, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(50, 50, 50, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public boolean onExitGui(final int button) {
        return this.focused = false;
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (button == 0) {
            this.mouseDown = true;
        }
        else if (button == 1 && this.focused) {
            try {
                final String text = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (!this.setText(text)) {
                    Athena.INSTANCE.getNotificationManager().showNotification("Clipboard doesnt match the desired data.", Color.RED);
                }
                else {
                    this.index = text.length();
                }
            }
            catch (HeadlessException | UnsupportedFlavorException | IOException ex2) {
                final Exception ex;
                final Exception e = ex;
                Athena.INSTANCE.getNotificationManager().showNotification("Invalid clipboard data.", Color.RED);
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public boolean passesThrough() {
        if (this.disabled) {
            return true;
        }
        if (this.focused) {
            return false;
        }
        if (this.mouseDown) {
            final int x = this.getRenderX();
            final int y = this.getRenderY();
            final int mouseX = this.parent.getMouseX();
            final int mouseY = this.parent.getMouseY();
            if (mouseX >= x && mouseX <= x + this.width + this.minOffset * 2 && mouseY >= y && mouseY <= y + this.height) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onKeyDown(final char character, final int key) {
        if (this.focused) {
            final int oldIndex = this.index;
            boolean found = true;
            boolean wantRender = false;
            switch (key) {
                case 28: {
                    this.onEnter();
                    this.focused = false;
                    break;
                }
                case 15: {
                    if (this.tab) {
                        break;
                    }
                    this.onTab();
                    this.focused = false;
                    break;
                }
                case 203: {
                    if (this.index - 1 >= 0) {
                        --this.index;
                    }
                    wantRender = true;
                    break;
                }
                case 205: {
                    if (this.index + 1 <= this.text.length()) {
                        ++this.index;
                    }
                    wantRender = true;
                    break;
                }
                case 14:
                case 211: {
                    if (!this.text.isEmpty() && this.index - 1 >= 0) {
                        this.text = new StringBuilder(this.text).deleteCharAt(this.index - 1).toString();
                        this.onAction();
                    }
                    if (this.index - 1 >= 0) {
                        --this.index;
                    }
                    wantRender = true;
                    break;
                }
                default: {
                    found = false;
                    break;
                }
            }
            if (wantRender && oldIndex != this.index) {
                this.lineTime = this.getLinePrediction();
            }
            if (found) {
                return;
            }
            if (this.pattern != TextPattern.NONE) {
                if (this.pattern == TextPattern.NUMBERS_ONLY && !Character.isDigit(character) && (this.text.length() != 0 || character != '-')) {
                    return;
                }
                if (this.pattern == TextPattern.TEXT_ONLY && !Character.isAlphabetic(character)) {
                    return;
                }
                if (this.pattern == TextPattern.TEXT_AND_NUMBERS && character != ' ' && !Character.isAlphabetic(character) && !Character.isDigit(character)) {
                    return;
                }
            }
            if ((character + "").matches("[A-Za-z0-9\\s_\\+\\-\\.,!@\ufffd#\\$%\\^&\\*\\(\\);\\\\/\\|<>\"'\\[\\]\\?=]")) {
                try {
                    if (this.pattern == TextPattern.NUMBERS_ONLY && character != '-') {
                        Integer.valueOf(new StringBuilder(this.text).insert(this.index, character).toString());
                    }
                    this.lineTime = this.getLinePrediction();
                    this.text = new StringBuilder(this.text).insert(this.index, character).toString();
                    ++this.index;
                    this.onAction();
                }
                catch (NumberFormatException ex) {}
            }
        }
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width + this.minOffset * 2;
        final int height = this.height;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (this.mouseDown) {
            this.focused = false;
        }
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                state = ButtonState.HOVER;
                if (this.mouseDown) {
                    this.onClick();
                    this.focused = true;
                    this.lineTime = System.currentTimeMillis();
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        this.setPriority(this.focused ? MenuPriority.HIGH : MenuPriority.MEDIUM);
        this.lastState = state;
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
        DrawUtils.drawRoundedRect(x - 1, y - 1, x + width + 1, y + height + 1, 4.0f, lineColor);
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, new Color(35, 35, 35, 255).getRGB());
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
        int labelWidth = this.getStringWidth(textToDraw + 1);
        int comp = 0;
        int toRender = this.index;
        while (labelWidth >= width) {
            if (comp < this.index) {
                textToDraw = textToDraw.substring(1);
                labelWidth = this.getStringWidth(textToDraw + 1);
                --toRender;
            }
            else if (comp > this.index) {
                textToDraw = textToDraw.substring(0, textToDraw.length() - 1);
                labelWidth = this.getStringWidth(textToDraw + 1);
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
            this.drawVerticalLine(x + this.getStringWidth(textToDraw.substring(0, toRender)) + 1, y + 2, height - 3, 1, textColor);
        }
        final int renderIndex = comp;
        final int renderStopIndex = comp + textToDraw.length();
        while (this.index > this.text.length()) {
            --this.index;
        }
        this.drawText(textToDraw, x + this.minOffset, y + height / 2 - this.getStringHeight(textToDraw) / 2, textColor);
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
                    position += this.getStringWidth(this.text.charAt(j) + "");
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
    
    protected long getLinePrediction() {
        return System.currentTimeMillis() - this.lineRefreshTime / 2;
    }
    
    public int getIntValue() {
        if (this.pattern == TextPattern.NUMBERS_ONLY && this.text.equals("-")) {
            return 0;
        }
        try {
            return Integer.valueOf(this.text);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public String getText() {
        if (this.pattern == TextPattern.NUMBERS_ONLY && this.text.equals("-")) {
            return "-0";
        }
        return this.text;
    }
    
    public boolean setText(final String text) {
        final StringBuilder builder = new StringBuilder();
        if (this.pattern == TextPattern.NONE) {
            this.text = text;
            return true;
        }
        for (final char character : text.toCharArray()) {
            if (this.pattern == TextPattern.NUMBERS_ONLY && !Character.isDigit(character) && (builder.length() != 0 || text.length() <= 0 || character != '-')) {
                return false;
            }
            if (this.pattern == TextPattern.TEXT_ONLY && !Character.isAlphabetic(character)) {
                return false;
            }
            if (this.pattern == TextPattern.TEXT_AND_NUMBERS && character != ' ' && !Character.isAlphabetic(character) && !Character.isDigit(character)) {
                return false;
            }
            if ((character + "").matches("[A-Za-z0-9\\s_\\+\\-\\.,!@\ufffd#\\$%\\^&\\*\\(\\);\\\\/\\|<>\"'\\[\\]\\?=]")) {
                try {
                    if (this.pattern == TextPattern.NUMBERS_ONLY && character != '-') {
                        Integer.valueOf(new StringBuilder(builder.toString()).insert(builder.toString().length(), character).toString());
                    }
                    builder.append(character);
                }
                catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        this.text = builder.toString();
        return true;
    }
    
    public void setCursor(final int i) {
        if (i >= 0) {
            this.index = i;
        }
    }
    
    public boolean isPasswordField() {
        return this.passwordField;
    }
    
    public void setPasswordField(final boolean passwordField) {
        this.passwordField = passwordField;
    }
    
    public boolean isFocused() {
        return this.focused;
    }
    
    public void setFocused(final boolean focused, final boolean tab) {
        if (focused) {
            this.lineTime = System.currentTimeMillis();
        }
        if (tab) {
            this.tab = true;
        }
        this.focused = focused;
    }
    
    public void onClick() {
    }
    
    public void onTab() {
    }
    
    public void onEnter() {
    }
    
    public void onAction() {
    }
}
