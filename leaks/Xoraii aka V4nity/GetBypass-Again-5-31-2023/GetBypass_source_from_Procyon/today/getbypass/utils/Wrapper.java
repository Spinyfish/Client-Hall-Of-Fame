// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;

public class Wrapper
{
    public static Minecraft mc;
    public static FontRenderer fr;
    
    static {
        Wrapper.mc = Minecraft.getMinecraft();
        Wrapper.fr = Wrapper.mc.fontRendererObj;
    }
    
    public PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }
}
