package Reality.Realii.utils.render.renderManager;

import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;

public class Rect {
    float x, y, width, height;
    Color color;
    Runnable runnable;

    public Rect(float x, float y, float width, float height, Color color, Runnable runnable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.runnable = runnable;
    }


    public void render(float mouseX, float mouseY) {
        if (isHovered(x, y, x + width, y + height, mouseX, mouseY)) {
            runnable.run();
        }
        RenderUtil.drawRect(x, y, x + width, y + height, color.getRGB());
    }

    public static boolean isHovered(float x, float y, float x2, float y2, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }
}
