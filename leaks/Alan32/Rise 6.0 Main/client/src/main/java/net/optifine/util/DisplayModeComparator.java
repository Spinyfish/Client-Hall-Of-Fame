package net.optifine.util;

import org.lwjgl.opengl.DisplayMode;

import java.util.Comparator;

public class DisplayModeComparator implements Comparator {
    public int compare(final Object o1, final Object o2) {
        final DisplayMode displaymode = (DisplayMode) o1;
        final DisplayMode displaymode1 = (DisplayMode) o2;
        return displaymode.getWidth() != displaymode1.getWidth() ? displaymode.getWidth() - displaymode1.getWidth() : (displaymode.getHeight() != displaymode1.getHeight() ? displaymode.getHeight() - displaymode1.getHeight() : (displaymode.getBitsPerPixel() != displaymode1.getBitsPerPixel() ? displaymode.getBitsPerPixel() - displaymode1.getBitsPerPixel() : (displaymode.getFrequency() != displaymode1.getFrequency() ? displaymode.getFrequency() - displaymode1.getFrequency() : 0)));
    }
}
