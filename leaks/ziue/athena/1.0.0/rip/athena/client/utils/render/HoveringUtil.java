package rip.athena.client.utils.render;

public class HoveringUtil
{
    public static boolean isHovering(final float x, final float y, final float width, final float height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
