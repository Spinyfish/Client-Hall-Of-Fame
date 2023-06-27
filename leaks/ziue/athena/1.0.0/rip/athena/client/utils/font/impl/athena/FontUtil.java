package rip.athena.client.utils.font.impl.athena;

import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import java.awt.*;
import java.io.*;
import net.minecraft.client.*;

public class FontUtil
{
    private static final IResourceManager RESOURCE_MANAGER;
    
    public static Font getResource(final String resource, final int size) {
        try {
            return Font.createFont(0, FontUtil.RESOURCE_MANAGER.getResource(new ResourceLocation(resource)).getInputStream()).deriveFont((float)size);
        }
        catch (FontFormatException | IOException ex2) {
            final Exception ex;
            final Exception ignored = ex;
            return null;
        }
    }
    
    static {
        RESOURCE_MANAGER = Minecraft.getMinecraft().getResourceManager();
    }
}
