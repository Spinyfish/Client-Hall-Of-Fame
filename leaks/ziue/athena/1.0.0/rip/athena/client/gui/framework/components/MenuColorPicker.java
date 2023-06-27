package rip.athena.client.gui.framework.components;

import java.awt.*;
import net.minecraft.util.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import org.lwjgl.input.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import java.awt.image.*;
import net.minecraft.client.renderer.texture.*;

public class MenuColorPicker extends MenuComponent
{
    protected Color color;
    protected Color temp;
    protected Point startPos;
    protected ButtonState lastState;
    protected ResourceLocation colorPickerMain;
    protected boolean mouseDown;
    protected int lastYPress;
    protected int startType;
    protected boolean wantsToDrag;
    protected boolean mouseDragging;
    protected boolean pickingColor;
    protected boolean canPick;
    protected int size;
    protected int colorOffset;
    protected int alphaOffset;
    protected int pickerWindowWidth;
    protected int pickerWindowHeight;
    protected MenuSlider alphaSlider;
    
    public MenuColorPicker(final int x, final int y, final int width, final int height, final int defaultColor) {
        super(x, y, width, height);
        this.lastState = ButtonState.NORMAL;
        this.mouseDown = false;
        this.startType = 0;
        this.wantsToDrag = false;
        this.mouseDragging = false;
        this.pickingColor = false;
        this.canPick = true;
        this.size = 80;
        this.colorOffset = 10;
        this.alphaOffset = 10;
        this.pickerWindowWidth = this.size + this.colorOffset;
        this.pickerWindowHeight = this.size + this.alphaOffset;
        this.lastYPress = -1;
        final Color theColor = new Color(defaultColor, true);
        this.color = theColor;
        this.temp = theColor;
        (this.alphaSlider = new MenuSlider(1.0f, 0.0f, 1.0f, 1, 0, 0, this.pickerWindowWidth, 10) {
            @Override
            public void onAction() {
                MenuColorPicker.this.color = new Color(MenuColorPicker.this.color.getRed(), MenuColorPicker.this.color.getGreen(), MenuColorPicker.this.color.getBlue(), Math.round(MenuColorPicker.this.alphaSlider.getValue() * 255.0f));
                MenuColorPicker.this.onAction();
            }
        }).setValue(theColor.getAlpha() / 255.0f);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(81, 108, 255, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(50, 50, 50, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(100, 120, 255, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.POPUP, new Color(10, 10, 10, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(10, 10, 10, 255));
        this.setColor(DrawType.LINE, ButtonState.POPUP, new Color(100, 120, 255, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(200, 200, 200, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(225, 225, 225, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(235, 235, 235, 255));
        this.setColor(DrawType.TEXT, ButtonState.POPUP, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (button == 0 && this.alphaSlider.passesThrough()) {
            this.mouseDown = true;
        }
        this.alphaSlider.onMouseClick(button);
    }
    
    @Override
    public void onMouseClickMove(final int button) {
        if (button == 0 && this.alphaSlider.passesThrough()) {
            this.mouseDragging = true;
        }
        this.alphaSlider.onMouseClickMove(button);
    }
    
    @Override
    public boolean onExitGui(final int key) {
        if (this.pickingColor) {
            this.pickingColor = false;
        }
        this.alphaSlider.onExitGui(key);
        return false;
    }
    
    @Override
    public boolean passesThrough() {
        if (this.pickingColor) {
            return false;
        }
        if (this.disabled) {
            return true;
        }
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        if (this.startPos != null) {
            if (mouseX >= this.startPos.x && mouseX <= this.startPos.x + this.pickerWindowWidth && mouseY >= this.startPos.y && mouseY <= this.startPos.y + this.pickerWindowHeight) {
                return false;
            }
        }
        else if (this.mouseDown && mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height + 1) {
            return false;
        }
        return true;
    }
    
    @Override
    public void onPreSort() {
        if (this.alphaSlider.getParent() == null && this.getParent() != null) {
            this.alphaSlider.setParent(this.getParent());
        }
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height + 1) {
                state = ButtonState.HOVER;
            }
            if (this.startPos != null) {
                boolean hover = false;
                if (mouseX >= this.startPos.x && mouseX <= this.startPos.x + this.pickerWindowWidth && mouseY >= this.startPos.y && mouseY <= this.startPos.y + this.pickerWindowHeight + 1) {
                    hover = true;
                }
                if (hover && this.mouseDown) {
                    this.wantsToDrag = true;
                }
                this.pickingColor = ((this.mouseDown && hover) || (!this.mouseDown && this.pickingColor));
                if (this.pickingColor) {
                    state = ButtonState.HOVER;
                }
            }
            else if (state == ButtonState.HOVER && this.mouseDown) {
                this.pickingColor = true;
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (this.pickingColor) {
            this.setPriority(MenuPriority.HIGHEST);
        }
        else if (state == ButtonState.HOVER || state == ButtonState.HOVERACTIVE) {
            this.setPriority(MenuPriority.HIGH);
        }
        else {
            this.setPriority(MenuPriority.MEDIUM);
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        int index = 0;
        for (int h = y; h < y + this.height; ++h) {
            DrawImpl.drawRect(x + 1, h, this.width - 1, 1, this.disabled ? this.lightenColor(index, 7, this.color).getRGB() : this.darkenColor(index, 7, this.color).getRGB());
            ++index;
        }
        this.drawHorizontalLine(x, y, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, this.height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + this.height, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x + this.width, y + 1, this.height - 1, 1, lineColor);
        if (this.startType <= 0) {
            if (this.alphaSlider.getParent() == null) {
                this.alphaSlider.setParent(this.getParent());
            }
            this.alphaSlider.onPreSort();
        }
        this.drawPicker();
        if (this.wantsToDrag) {
            this.mouseDragging = Mouse.isButtonDown(0);
            this.wantsToDrag = this.mouseDragging;
        }
        this.mouseDown = false;
        this.mouseDragging = false;
    }
    
    public void drawPicker() {
        if (this.pickingColor) {
            int mouseX = this.parent.getMouseX();
            int mouseY = this.parent.getMouseY();
            final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.POPUP);
            final int lineColor = this.getColor(DrawType.LINE, this.lastState);
            if (!this.mouseDown) {
                this.canPick = true;
            }
            if (!this.wantsToDrag) {
                this.startType = 0;
            }
            if (this.startPos == null) {
                final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
                int windowX = mouseX;
                int windowY = mouseY;
                if (windowX + this.pickerWindowWidth >= res.getScaledWidth()) {
                    windowX -= this.pickerWindowWidth;
                }
                if (windowY + this.pickerWindowHeight >= res.getScaledHeight()) {
                    windowY -= this.pickerWindowHeight;
                }
                this.startPos = new Point(windowX, windowY);
                this.alphaSlider.setX(this.startPos.x);
                this.alphaSlider.setY(this.startPos.y + this.pickerWindowHeight - this.alphaSlider.getHeight());
                this.canPick = false;
            }
            if (this.canPick && this.startType == 0) {
                if (mouseY > this.startPos.y && mouseY < this.startPos.y + this.pickerWindowHeight - this.alphaSlider.getHeight()) {
                    if (mouseX > this.startPos.x + this.pickerWindowWidth - this.colorOffset && mouseX < this.startPos.x + this.pickerWindowWidth) {
                        this.startType = 1;
                    }
                    else if (mouseX > this.startPos.x && mouseX < this.startPos.x + this.size) {
                        this.startType = 2;
                    }
                }
                else {
                    this.startType = -1;
                    this.canPick = false;
                }
            }
            if (this.startType != 0) {
                if (this.startType == 2) {
                    if (mouseX >= this.startPos.x + this.pickerWindowWidth) {
                        mouseX = this.startPos.x + this.pickerWindowWidth - this.size - 1;
                    }
                    else if (mouseX <= this.startPos.x) {
                        mouseX = this.startPos.x + this.pickerWindowWidth - this.size + 1;
                    }
                }
                else if (mouseX >= this.startPos.x + this.pickerWindowWidth) {
                    mouseX = this.startPos.x + this.pickerWindowWidth - 1;
                }
                else if (mouseX <= this.startPos.x + this.size) {
                    mouseX = this.startPos.x + this.size + 1;
                }
                if (mouseY >= this.startPos.y + this.pickerWindowHeight - this.alphaSlider.getHeight()) {
                    mouseY = this.startPos.y + this.pickerWindowHeight - this.alphaSlider.getHeight() - 1;
                }
                else if (mouseY <= this.startPos.y) {
                    mouseY = this.startPos.y + 1;
                }
            }
            DrawImpl.drawRect(this.startPos.x + 1, this.startPos.y + 1, this.pickerWindowWidth - 1, this.pickerWindowHeight - 1, backgroundColor);
            this.drawHorizontalLine(this.startPos.x, this.startPos.y, this.pickerWindowWidth + 1, 1, lineColor);
            this.drawVerticalLine(this.startPos.x, this.startPos.y + 1, this.pickerWindowHeight - 1, 1, lineColor);
            this.drawHorizontalLine(this.startPos.x, this.startPos.y + this.pickerWindowHeight, this.pickerWindowWidth + 1, 1, lineColor);
            this.drawVerticalLine(this.startPos.x + this.pickerWindowWidth, this.startPos.y + 1, this.pickerWindowHeight - 1, 1, lineColor);
            if (this.colorPickerMain == null) {
                final BufferedImage bufferedPicker = new BufferedImage(this.size, this.size, 2);
                for (int y = 0; y < this.size; ++y) {
                    final float blackMod = 255.0f * y / this.size;
                    for (int x = 0; x < this.size; ++x) {
                        final Color color = new Color(this.clampColor(this.temp.getRed() - blackMod), this.clampColor(this.temp.getGreen() - blackMod), this.clampColor(this.temp.getBlue() - blackMod));
                        bufferedPicker.setRGB(x, y, color.getRGB());
                    }
                }
                final DynamicTexture texture = new DynamicTexture(bufferedPicker);
                bufferedPicker.getRGB(0, 0, bufferedPicker.getWidth(), bufferedPicker.getHeight(), texture.getTextureData(), 0, bufferedPicker.getWidth());
                final ResourceLocation resource = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("color-picker-active", texture);
                this.colorPickerMain = resource;
            }
            if ((this.mouseDown || this.mouseDragging || this.wantsToDrag) && this.canPick && this.startType == 2 && mouseX > this.startPos.x && mouseX < this.startPos.x + this.size && mouseY > this.startPos.y && mouseY < this.startPos.y + this.size) {
                final int y2 = mouseY - this.startPos.y;
                final float blackMod2 = 255.0f * y2 / this.size;
                this.color = new Color(this.clampColor(this.temp.getRed() - blackMod2), this.clampColor(this.temp.getGreen() - blackMod2), this.clampColor(this.temp.getBlue() - blackMod2));
                this.onAction();
            }
            this.drawImage(this.colorPickerMain, this.startPos.x + 1, this.startPos.y + 1, this.size - 1, this.size - 1);
            final float colorSpeed = this.size / 3.8f;
            float red = 275.0f;
            float green = 275.0f;
            float blue = 275.0f;
            for (int y3 = this.startPos.y + 1; y3 < this.startPos.y + this.size; ++y3) {
                if (red > 255.0f) {
                    --red;
                }
                if (green > 255.0f) {
                    --green;
                }
                if (blue > 255.0f) {
                    --blue;
                }
                if (red >= 255.0f && green >= 255.0f && blue > 0.0f) {
                    blue -= colorSpeed;
                }
                else if (red >= 255.0f && green > 0.0f && blue <= 0.0f) {
                    green -= colorSpeed;
                }
                else if (red >= 255.0f && green <= 0.0f && blue < 255.0f) {
                    blue += colorSpeed;
                }
                else if (red > 0.0f && green <= 0.0f && blue >= 255.0f) {
                    red -= colorSpeed;
                }
                else if (red <= 0.0f && green < 255.0f && blue >= 255.0f) {
                    green += colorSpeed;
                }
                else if (red <= 0.0f && green >= 255.0f && blue > 0.0f) {
                    blue -= colorSpeed;
                }
                else if (red < 255.0f && green >= 255.0f && blue <= 0.0f) {
                    red += colorSpeed;
                }
                for (int x2 = this.startPos.x + this.size + 1; x2 < this.startPos.x + this.pickerWindowWidth; ++x2) {
                    final Color color2 = new Color(this.clampColor(red), this.clampColor(green), this.clampColor(blue));
                    if ((this.mouseDown || this.mouseDragging || this.wantsToDrag) && this.canPick && this.startType == 1 && this.isInPixel(mouseX, mouseY, x2, y3)) {
                        this.temp = color2;
                        this.lastYPress = y3;
                        this.onMiniAction();
                        Minecraft.getMinecraft().getTextureManager().deleteTexture(this.colorPickerMain);
                        this.colorPickerMain = null;
                    }
                    if (this.lastYPress == -1 && Math.abs(color2.getRGB() - this.temp.getRGB()) < 3) {
                        this.lastYPress = y3;
                    }
                    this.drawPixel(x2, y3, color2.getRGB());
                }
            }
            if (this.lastYPress != -1) {
                this.drawHorizontalLine(this.startPos.x + this.pickerWindowWidth - this.colorOffset, this.lastYPress, this.colorOffset, 1, lineColor);
            }
            this.alphaSlider.onRender();
            this.drawVerticalLine(this.startPos.x + this.size, this.startPos.y + 1, this.pickerWindowHeight - this.alphaOffset - 1, 1, lineColor);
        }
        else if (this.startPos != null) {
            this.startPos = null;
            if (this.colorPickerMain != null) {
                Minecraft.getMinecraft().getTextureManager().deleteTexture(this.colorPickerMain);
                this.colorPickerMain = null;
            }
            this.alphaSlider.setX(Integer.MAX_VALUE);
            this.alphaSlider.setY(Integer.MAX_VALUE);
        }
    }
    
    private boolean isInPixel(final int mouseX, final int mouseY, final int x, final int y) {
        return mouseX == x && mouseY == y;
    }
    
    private int clampColor(final float color) {
        final int theColor = Math.round(color);
        if (theColor > 255) {
            return 255;
        }
        if (theColor < 0) {
            return 0;
        }
        return theColor;
    }
    
    protected Color darkenColor(final int index, final int modifier, final Color color) {
        int newRed = color.getRed() - index * modifier;
        int newGreen = color.getGreen() - index * modifier;
        int newBlue = color.getBlue() - index * modifier;
        if (newRed < 0) {
            newRed = 0;
        }
        if (newGreen < 0) {
            newGreen = 0;
        }
        if (newBlue < 0) {
            newBlue = 0;
        }
        return new Color(newRed, newGreen, newBlue, Math.round(this.alphaSlider.getValue() * 255.0f));
    }
    
    protected Color lightenColor(final int index, final int modifier, final Color color) {
        int newRed = color.getRed() + index * modifier;
        int newGreen = color.getGreen() + index * modifier;
        int newBlue = color.getBlue() + index * modifier;
        if (newRed > 255) {
            newRed = 255;
        }
        if (newGreen > 255) {
            newGreen = 255;
        }
        if (newBlue > 255) {
            newBlue = 255;
        }
        return new Color(newRed, newGreen, newBlue, color.getAlpha());
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final int color) {
        this.color = new Color(color);
        Minecraft.getMinecraft().getTextureManager().deleteTexture(this.colorPickerMain);
        this.colorPickerMain = null;
    }
    
    public Color getColorCategory() {
        return this.temp;
    }
    
    public void setColorCategory(final int color) {
        this.temp = new Color(color);
        this.lastYPress = -1;
    }
    
    public MenuSlider getAlphaSlider() {
        return this.alphaSlider;
    }
    
    public void onAction() {
    }
    
    public void onMiniAction() {
    }
}
