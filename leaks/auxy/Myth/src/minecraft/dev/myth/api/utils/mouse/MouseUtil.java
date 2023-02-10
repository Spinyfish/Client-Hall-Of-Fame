/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 19:46
 */
package dev.myth.api.utils.mouse;

public class MouseUtil {

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

}
