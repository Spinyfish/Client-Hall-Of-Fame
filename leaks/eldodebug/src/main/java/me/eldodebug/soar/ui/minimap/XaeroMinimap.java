package me.eldodebug.soar.ui.minimap;

import java.io.IOException;

import me.eldodebug.soar.ui.minimap.interfaces.InterfaceHandler;
import net.minecraft.client.Minecraft;

public class XaeroMinimap
{
    public static XaeroMinimap instance;
    public static Minecraft mc = Minecraft.getMinecraft();
    
    public void load() throws IOException {
        InterfaceHandler.loadPresets();
        InterfaceHandler.load();
    }
}
