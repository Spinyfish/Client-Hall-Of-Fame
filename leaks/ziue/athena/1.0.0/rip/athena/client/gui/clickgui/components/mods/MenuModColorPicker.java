package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.components.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.gui.framework.draw.*;
import org.lwjgl.input.*;

public class MenuModColorPicker extends MenuColorPicker
{
    public MenuModColorPicker(final int x, final int y, final int width, final int height, final int defaultColor) {
        super(x, y, width, height, defaultColor);
    }
    
    @Override
    public void onInitColors() {
        super.onInitColors();
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(43, 43, 43, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(53, 53, 53, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(48, 48, 48, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(59, 59, 59, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.drawHorizontalLine(x, y, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, this.height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + this.height, this.width + 1, 1, lineColor);
        this.drawVerticalLine(x + this.width, y + 1, this.height - 1, 1, lineColor);
        DrawImpl.drawRect(x + 2, y + 2, this.width - 3, this.height - 3, lineColor);
        int index = 0;
        for (int h = y; h < y + this.height - 5; ++h) {
            DrawImpl.drawRect(x + 3, h + 3, this.width - 5, 1, this.disabled ? this.lightenColor(index, 7, this.color).getRGB() : this.darkenColor(index, 7, this.color).getRGB());
            ++index;
        }
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
}
