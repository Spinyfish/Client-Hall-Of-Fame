/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 23:19
 */

package cc.swift.util;

import java.awt.*;

public class ColorUtil {
    public static final Color GRAY = new Color(32, 32, 32, 255);
    public static final Color DARK_GRAY = new Color(25, 25, 25, 255);
    public static final Color LIGHT_GRAY = new Color(37, 37, 37, 255);

    public static int rainbow(int delay, double speed) {
        double hue = (((System.currentTimeMillis() + 10) + delay) * (speed / 100.0)) % 360.0;
        return Color.HSBtoRGB((float) hue / 360.0f, 1.0f, 1.0f);
    }
}
