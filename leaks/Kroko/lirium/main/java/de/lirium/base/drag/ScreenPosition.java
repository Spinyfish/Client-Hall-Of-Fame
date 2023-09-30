package de.lirium.base.drag;

import de.lirium.util.interfaces.IMinecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ScreenPosition implements IMinecraft {

    private double x, y;

    public ScreenPosition (int x, int y) {
        this.setAbsolute(x, y);
    }
    public ScreenPosition (double x, double y) {
        this.setRelative(x, y);
    }

    public void setAbsolute (int x, int y) {
        ScaledResolution res = new ScaledResolution(mc);

        this.x = (double) x / res.getScaledWidth();
        this.y = (double) y / res.getScaledHeight();
    }

    public void setRelative (double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static ScreenPosition fromAbsolute (int x, int y) {
        return new ScreenPosition(x, y);
    }
    public static ScreenPosition fromRelativePosition (double x, double y) {
        return new ScreenPosition(x, y);
    }

    public int getAbsoluteX () {
        ScaledResolution res = new ScaledResolution(mc);
        return (int) (x * res.getScaledWidth());
    }
    public int getAbsoluteY () {
        ScaledResolution res = new ScaledResolution(mc);
        return (int) (y * res.getScaledHeight());
    }
    public double getRelativeX () {
        return x;
    }
    public double getRelativeY () {
        return y;
    }


}
