package dev.elysium.client.ui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class Widget {
    public String name;
    public double x = 50;
    public double y = 50;
    public double width, height;
    public boolean dragging = false;

    public int corner = 0;

    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    public Widget(String name, double width, double height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public void draw() {
        GlStateManager.pushMatrix();
        if(corner == 0) {
            GlStateManager.translate(x, y, 0);
        } else if(corner == 1) {
            GlStateManager.translate(sr.getScaledWidth() - x, y, 0);
        } else if(corner == 2) {
            GlStateManager.translate(sr.getScaledWidth() - x, sr.getScaledHeight() - y, 0);
        } else if(corner == 3) {
            GlStateManager.translate(x, sr.getScaledHeight() - y, 0);
        }
    }
}
