/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:18
 */

package cc.swift.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MouseUtil {
    public boolean isHovered(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
