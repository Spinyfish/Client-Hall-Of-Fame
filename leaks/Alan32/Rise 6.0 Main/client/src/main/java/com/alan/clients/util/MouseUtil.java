package com.alan.clients.util;

public class MouseUtil {

    /**
     * Checks if mouse coordinates are within the bounds of given coordinates.
     *
     * @param x      The x coordinate of the top-left corner of the area.
     * @param y      The y coordinate of the top-left corner of the area.
     * @param width  The width of the area.
     * @param height The height of the area.
     * @param mouseX The x coordinate of the mouse.
     * @param mouseY The y coordinate of the mouse.
     * @return True if the mouse is within the area, false otherwise.
     */
    public static boolean isHovered(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
