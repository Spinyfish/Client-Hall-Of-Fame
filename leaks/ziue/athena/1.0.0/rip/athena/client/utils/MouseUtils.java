package rip.athena.client.utils;

import org.lwjgl.input.*;

public class MouseUtils
{
    public static Scroll scroll() {
        final int mouse = Mouse.getDWheel();
        if (mouse > 0) {
            return Scroll.UP;
        }
        if (mouse < 0) {
            return Scroll.DOWN;
        }
        return null;
    }
    
    public static boolean isInside(final int mouseX, final int mouseY, final double x, final double y, final double width, final double height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
    
    public enum Scroll
    {
        UP, 
        DOWN;
    }
}
