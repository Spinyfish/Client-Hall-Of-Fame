package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import rip.athena.client.gui.framework.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import org.lwjgl.input.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class MenuModKeybind extends MenuComponent
{
    public static final int mouseOffset = 500;
    protected boolean mouseDown;
    protected ButtonState lastState;
    protected boolean binding;
    protected int bind;
    
    public MenuModKeybind(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.bind = 0;
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(43, 43, 43, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(68, 68, 68, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(58, 58, 58, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(82, 82, 82, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(150, 150, 150, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(162, 162, 162, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(182, 182, 182, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(100, 100, 100, 255));
    }
    
    @Override
    public boolean onExitGui(final int key) {
        if (!this.binding) {
            return false;
        }
        if (key == 1) {
            this.onKeyDown(' ', 0);
            return true;
        }
        this.binding = false;
        return true;
    }
    
    @Override
    public void onKeyDown(final char character, final int key) {
        if (this.binding) {
            this.bind = key;
            this.binding = false;
            this.onAction();
        }
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (this.binding) {
            this.onKeyDown(' ', button - 500);
            return;
        }
        if (button == 0) {
            this.mouseDown = true;
        }
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = this.binding ? ButtonState.ACTIVE : ButtonState.NORMAL;
        if (!this.disabled) {
            boolean updated = false;
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height + 1) {
                state = (this.binding ? ButtonState.HOVERACTIVE : ButtonState.HOVER);
                if (this.mouseDown) {
                    this.binding = true;
                    updated = true;
                }
            }
            if (this.binding && !updated && this.mouseDown) {
                this.binding = false;
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        if (this.binding) {
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
    public boolean passesThrough() {
        if (this.disabled) {
            return true;
        }
        if (this.mouseDown) {
            final int x = this.getRenderX();
            final int y = this.getRenderY();
            final int mouseX = this.parent.getMouseX();
            final int mouseY = this.parent.getMouseY();
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
                return false;
            }
        }
        return !this.binding;
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = this.width;
        final int height = this.height;
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        RoundedUtils.drawRoundedGradientOutlineCorner((float)x, (float)y, (float)(x + width), (float)(y + height), 1.0f, 12.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor().getRGB());
        String text = "CLICK TO BIND";
        if (this.binding) {
            text = "PRESS A KEY";
        }
        else if (this.getBind() < 0 && this.getBind() + 500 >= 0) {
            String name = "";
            final int key = this.getBind() + 500;
            if (key == 0) {
                name = "MOUSE 1";
            }
            else if (key == 1) {
                name = "MOUSE 2";
            }
            else if (key == 2) {
                name = "MOUSE 3";
            }
            else if (key == 3) {
                name = "MOUSE 4";
            }
            else if (key == 4) {
                name = "MOUSE 5";
            }
            text = "BOUND: " + name.toUpperCase();
        }
        else if (this.isBound()) {
            text = "BOUND: " + Keyboard.getKeyName(this.getBind()).toUpperCase();
        }
        this.drawText(text, x + width / 2 - this.getStringWidth(text) / 2, y + height / 2 - this.getStringHeight(text) / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        this.mouseDown = false;
    }
    
    @Override
    public void drawText(final String text, final int x, final int y, final int color) {
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(text, x, y, color);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
        }
    }
    
    @Override
    public int getStringWidth(final String string) {
        if (Settings.customGuiFont) {
            return FontManager.getProductSansRegular(30).width(string);
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
    }
    
    @Override
    public int getStringHeight(final String string) {
        if (Settings.customGuiFont) {
            return (int)rip.athena.client.font.FontManager.baloo17.getHeight(string);
        }
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
    
    public boolean isBound() {
        return this.bind != 0;
    }
    
    public int getBind() {
        return this.bind;
    }
    
    public void setBind(final int bind) {
        this.bind = bind;
    }
    
    public void onAction() {
    }
}
