package rip.athena.client.utils.input;

import java.awt.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;

public class InputUtils
{
    public static Point getMousePos() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution sr = new ScaledResolution(mc);
        final int[] sizes = getWindowsSize();
        final int x = Mouse.getX() * sizes[0] / mc.displayWidth;
        final int y = sizes[1] - Mouse.getY() * sizes[1] / mc.displayHeight - 1;
        return new Point(x, y);
    }
    
    public static int[] getWindowsSize() {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return new int[] { sr.getScaledWidth(), sr.getScaledHeight() };
    }
}
