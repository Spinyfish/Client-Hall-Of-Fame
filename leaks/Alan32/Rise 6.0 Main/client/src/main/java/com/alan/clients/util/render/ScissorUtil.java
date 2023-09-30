package com.alan.clients.util.render;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScissorUtil {

    public static void enable() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void disable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void scissor(ScaledResolution scaledResolution, double x, double y, double width, double height) {
        if (x + width == x || y + height == y || x < 0 || y + height < 0) return;
        final int scaleFactor = scaledResolution.getScaleFactor();
        GL11.glScissor((int) Math.round(x * scaleFactor), (int) Math.round((scaledResolution.getScaledHeight() - (y + height)) * scaleFactor), (int) Math.round(width * scaleFactor), (int) Math.round(height * scaleFactor));
    }
}
