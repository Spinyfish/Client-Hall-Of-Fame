/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.collection.visual;

import org.lwjgl.input.Mouse;

public class MouseUtil {

    public enum ScrollDirection {
        UP, DOWN
    }

    public static ScrollDirection getDirection() {
        int direction = Mouse.getDWheel();

        if (direction > 0) {
            return ScrollDirection.UP;
        } else if (direction < 0) {
            return ScrollDirection.DOWN;
        } else {
            return null;
        }
    }

    public static int getWheel() {
        return Mouse.getDWheel();
    }

    public static boolean isInside(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
