package dev.africa.pandaware.utils.client;

import dev.africa.pandaware.utils.math.vector.Vec2i;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MouseUtils {
    public boolean isMouseInBounds(double mouseX, double mouseY, double x, double y, double x1, double y1) {
        return mouseX >= x && mouseX <= x1 && mouseY >= y && mouseY <= y1;
    }

    public boolean isMouseInBounds(Vec2i mouse, Vec2i pos, Vec2i size) {
        return isMouseInBounds(mouse.getX(), mouse.getY(), pos.getX(), pos.getY(),
                pos.getX() + size.getX(), pos.getY() + size.getY());
    }
}
