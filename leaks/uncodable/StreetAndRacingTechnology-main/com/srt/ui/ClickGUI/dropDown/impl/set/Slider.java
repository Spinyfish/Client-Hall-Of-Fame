package com.srt.ui.ClickGUI.dropDown.impl.set;

import java.awt.Color;
import java.text.DecimalFormat;

import com.srt.module.visuals.Hud;
import com.srt.settings.settings.NumberSetting;
import com.srt.ui.ClickGUI.dropDown.ClickGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Slider extends SetComp {

    private boolean dragging = false;
    private double x;
    private double y;
    private static double height = 12.5;
    private boolean hovered;
    private NumberSetting set;

    public Slider(NumberSetting s, com.srt.ui.ClickGUI.dropDown.impl.Button b) {
        super(s, b, height);
        this.set = s;
    }

    @Override
    public double drawScreen(int mouseX, int mouseY, double x, double y) {
        this.hovered = this.isHovered(mouseX, mouseY);
        this.x = x;
        this.y = y;
        
        this.height = 13;
        
        Gui.drawRect(x, y, x + this.parent.getWidth(), y + height + 1, ClickGui.getSecondaryColor().getRGB());
        if (this.dragging) {
            float toSet = (float) ((float) mouseX - (float) (this.x - 2)) / (float) (this.parent.getWidth() - 1);
            if (toSet > 1) {
                toSet = 1;
            }
            if (toSet < 0) {
                toSet = 0;
            }
            this.set.setValue(((this.set.getMax() - this.set.getMin()) * toSet) + this.set.getMin());
        }
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float distance = (float) ((this.set.getValue() - this.set.getMin()) / (this.set.getMax() - this.set.getMin()));
        Gui.drawRect(this.x, this.y, this.x + this.parent.getWidth(), this.y + this.height, new Color(21, 21, 21).getRGB());
        String name = this.set.getName() + " " + new DecimalFormat("#.##").format(this.set.getValue());
        Gui.drawRect(this.x + 1, this.y + font.FONT_HEIGHT - 8, (int) (this.x -1 + (this.parent.getWidth() * 1)), this.y + this.height - 1, ClickGui.getSecondaryColor().brighter().getRGB());
        Gui.drawRect(this.x + 1, this.y + font.FONT_HEIGHT - 8, (int) (this.x - 1+ (this.parent.getWidth() * distance)), this.y + this.height - 1, this.hovered ? ClickGui.getMainColor().darker().getRGB():Hud.getColorInt(2));
        GlStateManager.pushMatrix();
        float scale = 1;
        GlStateManager.scale(scale, scale, scale);
        font.drawStringWithShadow(name, (float)((this.x + 2) / scale + 2), (float)((y + 2) / scale), -1);
        GlStateManager.popMatrix();
        return this.height;
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (button == 0 && this.hovered) {
            this.dragging = !this.dragging;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x + 1 && mouseX <= x + 10+ this.parent.getWidth() && mouseY >= y && mouseY <= y + height;
    }
}
