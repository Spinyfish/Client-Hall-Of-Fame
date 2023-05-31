package de.lirium.util.render;

import de.lirium.base.feature.Feature;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class MouseUtil implements Feature {

    public static int[] getMouseCoords() {
        final ScaledResolution resolution = new ScaledResolution(mc);
        return new int[] { Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth,
                resolution.getScaledHeight() - Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight - 1 };
    }

    public static int getMouseX() {
        return getMouseCoords()[0];
    }

    public static int getMouseY() {
        return getMouseCoords()[1];
    }

    @Override
    public String getName() {
        return "Mouse Util";
    }
}