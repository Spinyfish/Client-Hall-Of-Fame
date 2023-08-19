package dev.thread.api.util.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {
    public float clamp(float num, float min, float max){
        return Math.max(Math.min(num, max), min);
    }

    public double clamp(double num, double min, double max){
        return Math.max(Math.min(num, max), min);
    }

    public int clamp(int num, int min, int max){
        return Math.max(Math.min(num, max), min);
    }

    public boolean isMouseHovered(int mouseX, int mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}