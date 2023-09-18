package net.optifine.util;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        DisplayMode displaymode = (DisplayMode)o1;
        DisplayMode displaymode1 = (DisplayMode)o2;

        if (displaymode.getWidth() != displaymode1.getWidth())
        {
            return displaymode.getWidth() - displaymode1.getWidth();
        }
        else if (displaymode.getHeight() != displaymode1.getHeight())
        {
            return displaymode.getHeight() - displaymode1.getHeight();
        }
        else if (displaymode.getBitsPerPixel() != displaymode1.getBitsPerPixel())
        {
            return displaymode.getBitsPerPixel() - displaymode1.getBitsPerPixel();
        }
        else
        {
            return displaymode.getFrequency() != displaymode1.getFrequency() ? displaymode.getFrequency() - displaymode1.getFrequency() : 0;
        }
    }
}
